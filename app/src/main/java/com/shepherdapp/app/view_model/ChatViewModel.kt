package com.shepherdapp.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.ShepherdApp.Companion.db
import com.shepherdapp.app.data.dto.DeleteChat
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.data.dto.chat.*
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.push_notification.FCMResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.chat_repository.ChatRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Nikita Kohli
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) :
    BaseViewModel() {

    private val TAG = "CarePointsViewModel"
    var chatModel: ChatModel? = null

    //    var chatListData: CareTeamChatListData? = null
    var isListenerInitialized: Boolean = false
    private var lastDocument: DocumentSnapshot? = null
    private var allMsgList: ArrayList<MessageData> = ArrayList()
    var messageListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null
    var tableName: String? = null
    var usersTableName: String? = null
    var roomId: String? = null

    var userAssignes: UserAssigneDetail? = null
    private var chatResponseData = MutableLiveData<Event<DataResult<MessageGroupResponse>>>()
    fun getChatMessages(): LiveData<Event<DataResult<MessageGroupResponse>>> = chatResponseData

    private var _noChatDataFoundLiveData = MutableLiveData<Event<Boolean>>()
    var noChatDataFoundLiveData: LiveData<Event<Boolean>> = _noChatDataFoundLiveData

    var fcmResponseLiveData = MutableLiveData<Event<DataResult<FCMResponseModel>>>()


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val addedCarePointLiveData = MutableLiveData<SingleEvent<Int>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openChatItemsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openMemberDetails: LiveData<SingleEvent<Int>> get() = addedCarePointLiveData

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun openEventChat(item: Int) {
        openChatItemsPrivate.value = SingleEvent(item)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
    fun getLovedOneId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_ID, "")

    fun getCurrentUser(): UserProfile? {
        return userRepository.getCurrentUser()
    }


    fun getRoomDetails(
        roomId: String,
        onListen: (RoomDetailsResponse) -> Unit
    ): Task<DocumentSnapshot> {
        val docRef: DocumentReference = db.collection(tableName!!).document(roomId)
        var chatMessageDetails = RoomDetailsResponse()
        return docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document?.toObject(RoomDetailsResponse::class.java) != null) {
                    chatMessageDetails = document.toObject(RoomDetailsResponse::class.java)!!
                }

            }
            return@addOnCompleteListener onListen(chatMessageDetails)
        }

    }

    fun initChatListener() {
        isListenerInitialized = true
        chatResponseData.postValue(Event(DataResult.Loading()))
        Log.d(TAG, "initChatListener: ${roomId}")

        val chatDocReference =
            roomId?.let { ShepherdApp.db.collection(tableName!!).document(it) }

        var query = chatDocReference?.collection(TableName.MESSAGES)
            ?.orderBy("created", Query.Direction.ASCENDING)
//        query = query?.limit(30)

        messageListener?.remove()
        messageListener = null
        listenUnreadUpdates()

        messageListener = query?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                showLog(TAG, message = "get message list failure error >> ${e.message}")

                chatResponseData.postValue(Event(DataResult.Failure(exception = e)))
                return@addSnapshotListener
            }

            if (!snapshot!!.documentChanges.isNullOrEmpty()) {
                if (lastDocument == null) {
                    lastDocument =
                        snapshot.documentChanges[snapshot.documentChanges.size - 1].document

                }
            }
            showLog("MSG_TIME", "get callback message")

            for (document in snapshot.documentChanges) {

                when (document.type) {
                    DocumentChange.Type.MODIFIED -> {
                        showLog("MSG_TIME", "modified message${document.document.data}")
                        getFormattedMessageData(
                            document.document
                        )
                    }
                    DocumentChange.Type.REMOVED -> {
                        showLog("MSG_TIME", "removed message${document.document.data}")

                    }
                    DocumentChange.Type.ADDED -> {
                        showLog("MSG_TIME", "new message${document.document.data}")

                        getFormattedMessageData(
                            document.document
                        )
                    }

                    else -> {
                    }
                }
            }

            val groupList = allMsgList.sortMessages()
            val groupResponse = MessageGroupResponse(true, groupList)
            chatResponseData.postValue(Event(DataResult.Success(groupResponse)))
            updateReadByData()
        }
    }

    private fun listenUnreadUpdates() {

        val chatRef =
            roomId?.let { ShepherdApp.db.collection(tableName!!).document(it) }
        chatListener?.remove()
        chatListener = null
        chatListener = chatRef?.addSnapshotListener { value, error ->
            if (value?.metadata?.hasPendingWrites() == false) {
                if (value.data != null) {
                    val chatData = Gson().fromJson(
                        JSONObject(value.data!!).toString(),
                        CareTeamChatListData::class.java
                    )
                    val loggedInUserID = userRepository.getCurrentUser()?.userId.toString()
                    if (chatData.usersDataMap[loggedInUserID]?.unreadCount ?: 0 > 0) {
                        chatData.usersDataMap[loggedInUserID]?.unreadCount = 0
                        chatRef.update("users_data", chatData.usersDataMap.serializeToMap())
                    }
                }
            }
        }
    }

    private fun updateReadByData() {
        ShepherdApp.db.runTransaction { transaction ->
            allMsgList.forEach { message ->
                if (!message.readIds.contains(userRepository.getCurrentUser()?.userId.toString())) {
                    val docRef =
                        db.collection(tableName!!).document(roomId ?: "")
                            .collection(TableName.MESSAGES).document(message.id ?: "")
                    transaction.get(docRef).data?.let {
                        val messageModel = Gson().fromJson(
                            JSONObject(it).toString(),
                            MessageData::class.java
                        )
                        val loggedInUserID = userRepository.getCurrentUser()?.userId.toString()
                        messageModel.readIds.add(loggedInUserID)
                        transaction.update(docRef, "readIds", messageModel.readIds)
                        /*  if (messageModel.readIds.size >= chatListData?.userIDs?.size ?: 0) {
                              transaction.update(docRef, "isRead", true)
                          }*/
                    }

                }
            }
        }
    }

    private fun getFormattedMessageData(
        document: QueryDocumentSnapshot?
    ) {
        try {
            if (document?.data != null) {
                val messageModel = document.getMessageModelFromDoc()

                if (allMsgList.singleOrNull { it.id.equals(messageModel.id) } == null) {
                    allMsgList.add(messageModel)


                } else {
                    val index =
                        allMsgList.indexOfFirst { it.id.equals(messageModel.id) }
                    if (index >= 0) {
                        allMsgList[index] = messageModel
                    }
                }
            }
        } catch (e: JSONException) {
            showException(e)
        }
    }

    fun UserProfile.toChatUser(): ChatUserDetail {
        return ChatUserDetail().apply {
            id = this@toChatUser.userId.toString()
            name = this@toChatUser.firstname + " " + this@toChatUser.lastname
            imageUrl = this@toChatUser.profilePhoto ?: ""
        }
    }

    private fun createChatListData(
        chatType: Int?,
        userList: ArrayList<ChatUserDetail?>?
    ): CareTeamChatListData {
        return CareTeamChatListData().apply {
            userIDs = ArrayList<String>()
            usersDataMap = HashMap()
            userList?.forEach {
                userIDs?.add(it?.id ?: "")
                usersDataMap.put(it?.id ?: "", it)
            }
            //sort userIds before storing into firebase as whereEqualTo function matches the ids in the order it is stored
            userIDs?.sort()
            this.chatType = chatType
        }
    }


    fun getAndSaveMessageData(
        roomId: String,
        msgType: Int,
        imageFile: String = "",
        message: String? = ""
    ) {
        val data = MessageData().apply {
            content = message
            isRead = false
            senderID = userRepository.getCurrentUser()?.userId.toString()
            messageType = msgType
            readIds = ArrayList<String>().apply {
                add(userRepository.getCurrentUser()?.userId.toString())
            }
            senderName =
                userRepository.getCurrentUser()?.firstname + " " + userRepository.getCurrentUser()?.lastname
            senderProfilePic = userRepository.getCurrentUser()?.profilePhoto
            if (!imageFile.isNullOrEmpty()) {
                attachment = imageFile
            }
        }
        sendMessage(data, roomId)
    }

    private fun sendMessage(messageData: MessageData, roomId: String) {
        addMessageInDb(messageData, roomId)
    }

    private fun addMessageInDb(messageData: MessageData, roomId: String) {
        var userIDs: ArrayList<String>? = ArrayList()

        val chatReference = db.collection(tableName!!).document(roomId)

        chatReference.collection(TableName.MESSAGES)
            .add(messageData.serializeToMap())
            .addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )

                addLastMessage(roomId, messageData)
                sendNotification(messageData, userIDs)
            }
    }

    private fun sendNotification(
        messageData: MessageData,
        userIDs: ArrayList<String>?
    ) {
        val firebaseTokensList: ArrayList<String> = arrayListOf()

        // Get the userIDs in the group and find fireStore token from Users collection
        val memberIDs = userIDs?.filter {
            it != messageData.senderID
        } as ArrayList<String>

        Log.d(TAG, "memberIDs :$memberIDs ")
        // Get the firebase token of users using memberIds
        usersTableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE /*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.USERS
            } else {
                TableName.USERS_DEV
            }
        db.collection(usersTableName!!)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    if (data?.get("id") != null) {
                        val id = data["id"] as Number
                        memberIDs.forEach {
                            if (id.toInt() == it.toInt()) {
                                val firebaseToken = data["firebase_token"] as String
                                Log.d(TAG, "firebase Token :$firebaseToken")
                                firebaseTokensList.add(firebaseToken)
                            }
                        }
                    }
                }
                Log.d(TAG, "in : firebase Tokens are: $firebaseTokensList")
                val loggedInUser = userRepository.getCurrentUser()


                val notificationObject = JSONObject().apply {
                    val notifyBody = when (messageData.messageType) {
                        Chat.MESSAGE_IMAGE -> {
                            "Sent Image"
                        }
                        Chat.MESSAGE_VIDEO -> {
                            "Sent Video"
                        }
                        else -> {
                            messageData.content
                        }

                    }
                    put("title", messageData.senderName)
                    put("body", notifyBody)
                    put("from_name", messageData.senderName)
                    put("from_image", loggedInUser?.profilePhoto ?: "")
                    put("user_id", messageData.senderID)
                    put("room_id", roomId)
                    put("chat_type", Chat.CHAT_SINGLE)
                    put("sound", "default")
                    put("type", Const.NotificationAction.MESSAGE)
                }

                val jsArray = JSONArray()
                firebaseTokensList.forEach { jsArray.put(it) }
                val msgObject = JSONObject().apply {
                    put("data", notificationObject)
                    put("notification", notificationObject)
                    put("chat_type", Chat.CHAT_SINGLE)
                    put("user_id", loggedInUser?.userId)
                    put("registration_ids", jsArray)
                }

                viewModelScope.launch {
                    val notificationModel = Gson().fromJson(
                        msgObject.toString(),
                        CareTeamChatNotificationModel::class.java
                    )
                    val response = chatRepository.sendPushNotifications(notificationModel)
                    withContext(Dispatchers.Main) {
                        response.collect {
                            fcmResponseLiveData.postValue(Event(it))
                        }
                    }
                }
            }
//        Log.d(TAG, "firebase Tokens are: $firebaseTokensList")
    }

    var deleteChatUserIdListing: java.util.ArrayList<DeleteChat> = java.util.ArrayList()

    private fun addLastMessage(id: String?, messageData: MessageData) {
        val roomArray = roomId!!.split("-")
        //  Add LAST CHAT MESSAGE AND TIME


        val chatMessageDetails = ChatMessageDetails(
            deleteChatUserIdListing,
            messageData.content!!,
            roomId!!,
            UserDataMessages(
                getCurrentUser()?.id,
                getCurrentUser()?.userId,
                getCurrentUser()?.firstname!!,
                getCurrentUser()?.lastname,
                getCurrentUser()?.profilePhoto!!
            ),
            UserDataMessages(
                userAssignes!!.id,
                userAssignes!!.userId,
                userAssignes!!.firstname,
                userAssignes!!.lastname,
                userAssignes!!.profilePhoto,
            ),
            arrayListOf(roomArray[0].toLong(), roomArray[1].toLong()),
            System.currentTimeMillis() / 1000
        )

        db.collection(tableName!!).document(id!!)
            .set(chatMessageDetails)
    }


    fun getPreviousMessages() {
        chatResponseData.postValue(Event(DataResult.Loading()))


        //get message list

        val chatDocReference = db.collection(tableName!!).document(roomId!!)
        var query = chatDocReference.collection(TableName.MESSAGES)
            .orderBy("created", Query.Direction.DESCENDING)

        try {
            if (lastDocument != null && lastDocument?.contains("created")!!)
                query = query.startAfter(lastDocument!!)

            query = query.limit(10)

            query.get().addOnSuccessListener { querySnapshot ->

                if (!querySnapshot?.documents.isNullOrEmpty()) {
                    lastDocument = querySnapshot.documents[querySnapshot.documents.size - 1]
                }
                for (document in querySnapshot.documentChanges) {
                    try {
                        val messageModel = document.document.getMessageModelFromDoc()
                        showLog("MSG_TIME", "previous message $messageModel")
                        if (allMsgList.singleOrNull { it.id.equals(messageModel.id) } == null) {
                            allMsgList.add(messageModel)
                        } else {
                            val index =
                                allMsgList.indexOfFirst { it.id.equals(messageModel.id) }
                            if (index >= 0) {
                                allMsgList[index] = messageModel
                            }
                        }
                    } catch (e: JSONException) {
                        showException(e)
                    }
                }
                val groupList = allMsgList.sortMessages()
                val groupResponse = MessageGroupResponse(false, groupList)
                chatResponseData.postValue(Event(DataResult.Success(groupResponse)))
            }
                .addOnFailureListener {
                    chatResponseData.postValue(Event(DataResult.Failure(exception = it)))
                }
        } catch (e: Exception) {
            Log.e("catch_exception", "cath: ${e.message}")
        }

    }


}
