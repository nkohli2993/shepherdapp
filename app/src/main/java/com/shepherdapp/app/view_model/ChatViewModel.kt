package com.shepherdapp.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.ShepherdApp.Companion.db
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.added_events.*
import com.shepherdapp.app.data.dto.chat.*
import com.shepherdapp.app.data.dto.dashboard.LoveUser
import com.shepherdapp.app.data.dto.login.UserLovedOne
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
    var chatListData: CareTeamChatListData? = null
    var isListenerInitialized: Boolean = false
    private var lastDocument: DocumentSnapshot? = null
    private var allMsgList: ArrayList<MessageData> = ArrayList()
    var messageListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null
    var tableName: String? = null
    var usersTableName: String? = null

    private var chatResponseData = MutableLiveData<Event<DataResult<MessageGroupResponse>>>()
    fun getChatMessages(): LiveData<Event<DataResult<MessageGroupResponse>>> = chatResponseData

    private var _groupNameLiveData = MutableLiveData<Event<String>>()
    var groupNameLiveData: LiveData<Event<String>> = _groupNameLiveData

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

    //get userinfo from Shared Pref
    fun getLovedUserDetail(): LoveUser? {
        return userRepository.getLovedUser()
    }

    fun getLovedOneDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }

    //get userinfo from Shared Pref
    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun isLoggedInUserCareTeamLeader(): Boolean? {
        return userRepository.isLoggedInUserTeamLead()
    }


    fun isCarePointPermission(): Boolean? {
        return userRepository.isCarePointPermission()
    }


    fun setToUserDetail(
        chatType: Int?,
        toUsers: ArrayList<ChatUserDetail>?,
    ) {
        val loggedInUser = userRepository.getCurrentUser()
        val memberList = ArrayList<ChatUserDetail?>()
        toUsers?.let { memberList.addAll(it) }
        val loggedInChatUser = loggedInUser?.toChatUser()
        Log.d(TAG, "loggedIn User id:${loggedInChatUser?.id} ")
        val membersId = memberList.map {
            it?.id
        }
        if (!membersId.contains(loggedInChatUser?.id.toString())) {
            memberList.add(loggedInChatUser)
        }
        chatListData = createChatListData(chatType, memberList)
        Log.d(TAG, "setToUserDetail: ChatListData : $chatListData")

        findChatId()
    }

    private fun findChatId(
        isFirstTime: Boolean = true,
        onFound: (isFounded: Boolean) -> Unit = {}
    ) {
        val userIDs: ArrayList<String>? = if (isFirstTime) {
            chatListData?.userIDs
        } else {
            chatListData?.userIDs?.reversed() as ArrayList<String>
        }
        userIDs?.sort()

        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }

        db.collection(tableName!!)
            .whereArrayContains("users_data", userRepository.getUserId())
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {

                    // Get the document id of the messages
                    chatListData?.id = it.documents[0].id
                    Log.d(TAG, "findChatId:DocumentID is ${chatListData?.id} ")

                    if (it.documents[0] != null) {
                        chatListData = Gson().fromJson(
                            JSONObject(it.documents[0].data).toString(),
                            CareTeamChatListData::class.java
                        )
                        // Get the document id of the messages
                        chatListData?.id = it.documents[0].id
                    }
                    onFound(true)
                    initChatListener()

                } else {

                    // Enter the event id and data
                    db.collection(tableName!!).add(chatListData.serializeToMap())
                        .addOnSuccessListener {

                            db.collection(tableName!!).document(it.id)
                                .update("id", it.id)
                            chatListData?.id = it.id

                            initChatListener()
                        }

                }
            }.addOnFailureListener {
                if (BuildConfig.DEBUG) {
                    it.printStackTrace()
                    Log.d(TAG, "findChatId: Document id not found")
                }
                onFound(false)
            }
    }


    private fun initChatListener() {
        isListenerInitialized = true
        chatResponseData.postValue(Event(DataResult.Loading()))
        Log.d(TAG, "initChatListener: ${chatListData?.id}")
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        val chatDocReference =
            chatListData?.id?.let { ShepherdApp.db.collection(tableName!!).document(it) }

        var query = chatDocReference?.collection(TableName.MESSAGES)
            ?.orderBy("created", Query.Direction.DESCENDING)
        query = query?.limit(10)

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
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        val chatRef =
            chatListData?.id?.let { ShepherdApp.db.collection(tableName!!).document(it) }
        chatListener?.remove()
        chatListener = null
        chatListener = chatRef?.addSnapshotListener { value, error ->
            if (value?.metadata?.hasPendingWrites() == false) {
                if (value.data != null) {
                    val chatData = Gson().fromJson(
                        JSONObject(value.data!!).toString(),
                        ChatListData::class.java
                    )
                    val loggedInUserID = userRepository.getCurrentUser()?.id.toString()
                    if (chatData.usersDataMap[loggedInUserID]?.unreadCount ?: 0 > 0) {
                        chatData.usersDataMap[loggedInUserID]?.unreadCount = 0
                        chatRef.update("users_data", chatData.usersDataMap.serializeToMap())
                    }
                }
            }
        }
    }

    private fun updateReadByData() {
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }

        ShepherdApp.db.runTransaction { transaction ->
            allMsgList.forEach { message ->
                if (!message.readIds.contains(userRepository.getCurrentUser()?.id.toString())) {
                    val docRef =
                        db.collection(tableName!!).document(chatListData?.id ?: "")
                            .collection(TableName.MESSAGES).document(message.id ?: "")
                    transaction.get(docRef).data?.let {
                        val messageModel = Gson().fromJson(
                            JSONObject(it).toString(),
                            MessageData::class.java
                        )
                        val loggedInUserID = userRepository.getCurrentUser()?.id.toString()
                        messageModel.readIds.add(loggedInUserID)
                        transaction.update(docRef, "readIds", messageModel.readIds)
                        if (messageModel.readIds.size >= chatListData?.userIDs?.size ?: 0) {
                            transaction.update(docRef, "isRead", true)
                        }
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


    fun getAndSaveMessageData(msgType: Int, imageFile: String = "", message: String? = "") {
        val data = MessageData().apply {
            content = message
            isRead = false
            senderID = userRepository.getCurrentUser()?.userId.toString()
            messageType = msgType
            readIds = ArrayList<String>().apply {
                add(userRepository.getCurrentUser()?.userId.toString() ?: "")
            }
            senderName =
                userRepository.getCurrentUser()?.firstname + " " + userRepository.getCurrentUser()?.lastname
            senderProfilePic = userRepository.getCurrentUser()?.profilePhoto
            if (!imageFile.isNullOrEmpty()) {
                attachment = imageFile
            }
        }
        sendMessage(data)
    }

    private fun sendMessage(messageData: MessageData) {
        if (isListenerInitialized) {
            addMessageInDb(messageData)
        } else {
            createNewChat {
                if (it) {
                    addMessageInDb(messageData)
                }
            }
        }
    }

    private fun createNewChat(onChatCreated: (created: Boolean) -> Unit) {
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        if (chatListData?.id.isNullOrEmpty()) {

            ShepherdApp.db.collection(tableName!!).add(chatListData.serializeToMap())
                .addOnSuccessListener {

                    ShepherdApp.db.collection(tableName!!).document(it.id).update("id", it.id)
                    chatListData?.id = it.id

                    initChatListener()
                    onChatCreated(true)
                }
        } else {
            chatListData?.id?.let {
                ShepherdApp.db.collection(tableName!!).document(it)
                    .set(chatListData.serializeToMap())
                    .addOnSuccessListener {
                        initChatListener()
                        onChatCreated(true)
                    }
            }
        }

    }

    private fun addMessageInDb(messageData: MessageData) {
        var userIDs: ArrayList<String>? = ArrayList()
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        val chatReference = db.collection(tableName!!).document(chatListData?.id ?: "")
        //Get group name, event_id and userIds
        chatReference.get().addOnSuccessListener {
            val chatData = Gson().fromJson(
                it.data?.let { it1 -> JSONObject(it1).toString() },
                ChatListData::class.java
            )
            userIDs = chatData.userIDs
            Log.d(TAG, "userIDs:$userIDs")
        }

        chatReference.collection(TableName.MESSAGES)
            .add(messageData.serializeToMap())
            .addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )
                updateUnreadCount(chatReference, messageData, false)
                // Send Notification
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
                    put("chat_id", chatListData?.id)
                    put("chat_type", Chat.CHAT_SINGLE)
                    put("sound", "default")
                    put("type", Const.NotificationAction.MESSAGE)
                }

                val jsArray = JSONArray()
                firebaseTokensList.forEach { jsArray.put(it) }
                val msgObject = JSONObject().apply {
                    put("data", notificationObject)
                    put("notification", notificationObject)
                    put("chat_type", chatListData?.chatType)
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

    private fun updateUnreadCount(
        chatReference: DocumentReference,
        messageData: MessageData,
        isRead: Boolean
    ) {
        chatReference.get().addOnSuccessListener { documentSnapshot ->
            try {

                if (documentSnapshot != null && documentSnapshot.data != null) {
                    val chatData = Gson().fromJson(
                        JSONObject(documentSnapshot.data!!).toString(),
                        ChatListData::class.java
                    )


                    if (isRead) {
                        if (!chatData.senderId?.equals(userRepository.getCurrentUser()?.id.toString())!!) {
//                            chatDence.update("unread_count", chatData.unread_count)
                        }
                    } else {
                        chatData.usersDataMap.keys.forEach {
                            if (it != userRepository.getCurrentUser()?.id.toString()) {
                                var count = chatData.usersDataMap[it]?.unreadCount ?: 0
                                count += 1
                                chatData.usersDataMap[it]?.unreadCount = count
                            }
                        }
                        chatReference.update(
                            hashMapOf(
                                "latest_message" to messageData.content,
                                "last_message_type" to messageData.messageType,
                                "updated_at" to FieldValue.serverTimestamp(),
                                "users_data" to chatData.usersDataMap.serializeToMap(),
                                "sender_id" to messageData.senderID
                            ) as Map<String, Any?>
                        )
                    }
                }
            } catch (e: JSONException) {
                showException(e)
            }
        }
    }


    fun getPreviousMessages() {
        chatResponseData.postValue(Event(DataResult.Loading()))
        tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }

        //get message list

        val chatDocReference = db.collection(tableName!!).document(chatListData?.id!!)
        var query = chatDocReference.collection(TableName.MESSAGES)
            .orderBy("created", Query.Direction.DESCENDING)

        try{
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
        }catch (e:Exception){
            Log.e("catch_exception","cath: ${e.message}")
        }

    }


}
