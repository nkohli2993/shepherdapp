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
import com.shepherdapp.app.data.dto.added_events.*
import com.shepherdapp.app.data.dto.chat.*
import com.shepherdapp.app.data.dto.dashboard.LoveUser
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.push_notification.FCMResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_point.CarePointRepository
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


@HiltViewModel
class CreatedCarePointsViewModel @Inject constructor(
    private val carePointRepository: CarePointRepository,
    private val userRepository: UserRepository,
) :
    BaseViewModel() {

    private val TAG = "CarePointsViewModel"
    var chatModel: ChatModel? = null
    var chatListData: ChatListData? = null
    var isListenerInitialized: Boolean = false
    private var lastDocument: DocumentSnapshot? = null
    private var allMsgList: ArrayList<MessageData> = ArrayList()
    var messageListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null
    var groupName: String? = null
    var eventId: Int? = null

    private var chatResponseData = MutableLiveData<Event<DataResult<MessageGroupResponse>>>()
    fun getChatMessages(): LiveData<Event<DataResult<MessageGroupResponse>>> = chatResponseData

    private var _groupNameLiveData = MutableLiveData<Event<String>>()
    var groupNameLiveData: LiveData<Event<String>> = _groupNameLiveData

    private var _noChatDataFoundLiveData = MutableLiveData<Event<Boolean>>()
    var noChatDataFoundLiveData: LiveData<Event<Boolean>> = _noChatDataFoundLiveData

    var fcmResponseLiveData = MutableLiveData<Event<DataResult<FCMResponseModel>>>()
    // var fcmResponseLiveData: LiveData<Event<DataResult<FCMResponseModel>>> = _fcmResponseLiveData


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val addedCarePointLiveData = MutableLiveData<SingleEvent<Int>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openChatItemsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openMemberDetails: LiveData<SingleEvent<Int>> get() = addedCarePointLiveData

    // for care point listing
    private var _addedCarePointLiveData =
        MutableLiveData<Event<DataResult<AddedEventResponseModel>>>()
    var carePointsResponseLiveData: LiveData<Event<DataResult<AddedEventResponseModel>>> =
        _addedCarePointLiveData

    // for care point event detail
    private var _addedCarePointDetailLiveData =
        MutableLiveData<Event<DataResult<EventDetailResponseModel>>>()
    var carePointsResponseDetailLiveData: LiveData<Event<DataResult<EventDetailResponseModel>>> =
        _addedCarePointDetailLiveData

    // for care point event detail comments
    private var _addedCarePointDetailCommentsLiveData =
        MutableLiveData<Event<DataResult<AllCommentEventsResponseModel>>>()
    var addedCarePointDetailCommentsLiveData: LiveData<Event<DataResult<AllCommentEventsResponseModel>>> =
        _addedCarePointDetailCommentsLiveData

    //for comment
    private var _addedCarePointCommentLiveData =
        MutableLiveData<Event<DataResult<EventCommentResponseModel>>>()
    var addedCarePointCommentLiveData: LiveData<Event<DataResult<EventCommentResponseModel>>> =
        _addedCarePointCommentLiveData


    fun getCarePointsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        start_date: String,
        end_date: String, loved_one_user_uid: String
    ): LiveData<Event<DataResult<AddedEventResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getCarePointsAdded(
                    pageNumber,
                    limit,
                    start_date,
                    end_date,
                    loved_one_user_uid
                )
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointLiveData.postValue(Event(it)) }
            }
        }

        return carePointsResponseLiveData
    }

    fun getCarePointsDetailId(
        id: Int
    ): LiveData<Event<DataResult<EventDetailResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getCarePointsDetailIdBased(id)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointDetailLiveData.postValue(Event(it)) }
            }
        }
        return carePointsResponseDetailLiveData
    }

    fun getCarePointsEventCommentsId(
        page: Int, limit: Int, id: Int
    ): LiveData<Event<DataResult<AllCommentEventsResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getEventCommentsIdBased(page, limit, id)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointDetailCommentsLiveData.postValue(Event(it)) }
            }
        }
        return addedCarePointDetailCommentsLiveData
    }

    fun addEventCommentCarePoint(
        eventCommentModel: EventCommentModel
    ): LiveData<Event<DataResult<EventCommentResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.addEventComment(eventCommentModel)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointCommentLiveData.postValue(Event(it)) }
            }
        }
        return addedCarePointCommentLiveData
    }

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

    //get userinfo from Shared Pref
    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }


    fun setToUserDetail(
        chatType: Int?,
        toUsers: ArrayList<ChatUserDetail>?,
        groupName: String?,
        eventId: Int?
    ) {
        this.groupName = groupName
        this.eventId = eventId
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
        chatListData = createChatListData(chatType, memberList, groupName, eventId)
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

        db.collection(TableName.CHATS)
//            .whereEqualTo("userIDs", userIDs)
//            .whereEqualTo("group_name", groupName)
            .whereEqualTo("event_id", eventId)
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {

                    // Get the document id of the messages
                    chatListData?.id = it.documents[0].id
                    Log.d(TAG, "findChatId:DocumentID is ${chatListData?.id} ")

                    if (it.documents[0] != null) {
                        chatListData = Gson().fromJson(
                            JSONObject(it.documents[0].data).toString(),
                            ChatListData::class.java
                        )
                        if (chatListData?.chatType == Chat.CHAT_GROUP) {
                            val groupName = chatListData?.groupName
                            if (!groupName.isNullOrEmpty()) {
                                _groupNameLiveData.postValue(Event(groupName))
                            }
                        }
                    }
                    onFound(true)
                    initChatListener()

                } else {
                    /* if (isFirstTime) {
                         findChatId(isFirstTime = false) { isFounded ->
                             onFound(isFounded)
                         }
                     } else {
                         onFound(false)
 //                        _noChatDataFoundLiveData.postValue(Event(true))
                     }*/

                    // Enter the event id and data
                    db.collection(TableName.CHATS).add(chatListData.serializeToMap())
                        .addOnSuccessListener {

                            ShepherdApp.db.collection(TableName.CHATS).document(it.id)
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
        val chatDocReference =
            chatListData?.id?.let { ShepherdApp.db.collection(TableName.CHATS).document(it) }

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
        val chatRef =
            chatListData?.id?.let { ShepherdApp.db.collection(TableName.CHATS).document(it) }
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
        ShepherdApp.db.runTransaction { transaction ->
            allMsgList.forEach { message ->
                if (!message.readIds.contains(userRepository.getCurrentUser()?.id.toString())) {
                    val docRef =
                        db.collection(TableName.CHATS).document(chatListData?.id ?: "")
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
        userList: ArrayList<ChatUserDetail?>?,
        groupName: String?,
        eventId: Int?
    ): ChatListData {
        return ChatListData().apply {
            userIDs = ArrayList<String>()
            usersDataMap = HashMap()
            userList?.forEach {
                userIDs?.add(it?.id ?: "")
                usersDataMap.put(it?.id ?: "", it)
            }
            //sort userIds before storing into firebase as whereEqualTo function matches the ids in the order it is stored
            userIDs?.sort()
            this.chatType = chatType
            this.groupName = groupName
            this.eventID = eventId
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
        if (chatListData?.id.isNullOrEmpty()) {
            ShepherdApp.db.collection(TableName.CHATS).add(chatListData.serializeToMap())
                .addOnSuccessListener {

                    ShepherdApp.db.collection(TableName.CHATS).document(it.id).update("id", it.id)
                    chatListData?.id = it.id

                    initChatListener()
                    onChatCreated(true)
                }
        } else {
            chatListData?.id?.let {
                ShepherdApp.db.collection(TableName.CHATS).document(it)
                    .set(chatListData.serializeToMap())
                    .addOnSuccessListener {
                        initChatListener()
                        onChatCreated(true)
                    }
            }
        }

    }

    private fun addMessageInDb(messageData: MessageData) {
        var eventID: Int? = null
        var userIDs: ArrayList<String>? = ArrayList()
        val chatReference = db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        //Get group name, event_id and userIds
        chatReference.get().addOnSuccessListener {
            val chatData = Gson().fromJson(
                it.data?.let { it1 -> JSONObject(it1).toString() },
                ChatListData::class.java
            )
            eventID = chatData.eventID
            Log.d(TAG, "eventID:$eventID ")
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
//                sendNotification(messageData, userIDs, eventID)
            }
    }

    private fun sendNotification(
        messageData: MessageData,
        userIDs: ArrayList<String>?,
        eventID: Int?
    ) {
        var firebaseTokensList: ArrayList<String> = arrayListOf()

        // Get the userIDs in the group and find fireStore token from Users collection
        val memberIDs = userIDs?.filter {
            it != messageData.senderID
        } as ArrayList<String>

        Log.d(TAG, "memberIDs :$memberIDs ")
        // Get the firebase token of users using memberIds
        /*memberIDs.forEach {
            db.collection(TableName.USERS)
                .whereEqualTo("id", it.toInt())
                .get()
                .addOnSuccessListener { qs ->
                    if (!qs.documents.isNullOrEmpty()) {
                        val fToken = qs.documents[0].data?.get("firebase_token") as String
                        firebaseTokensList.add(fToken)
                        Log.d(TAG, "fToken for id : $it is $fToken")
                    }
                }
        }*/
//        Log.d(TAG, "firebase Tokens are: $firebaseTokensList")
        db.collection(TableName.USERS)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val data = document.data
                    val id = data?.get("id") as Double
                    memberIDs.forEach {
                        if (id.toInt() == it.toInt()) {
                            val firebaseToken = data["firebase_token"] as String
                            Log.d(TAG, "firebase Token :$firebaseToken")
                            firebaseTokensList.add(firebaseToken)
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
                    put("chat_type", Chat.CHAT_GROUP)
                    put("sound", "default")
                    put("type", Const.NotificationAction.MESSAGE)
                }

                val jsArray = JSONArray()
                firebaseTokensList.forEach { jsArray.put(it) }
                val msgObject = JSONObject().apply {
                    put("data", notificationObject)
                    put("chat_type", chatListData?.chatType)
                    put("group_id", chatListData?.eventID)
                    put("user_id", loggedInUser?.userId)
                    put("registration_ids", jsArray)
                }

                viewModelScope.launch {
                    val notificationModel = Gson().fromJson(
                        msgObject.toString(),
                        ChatNotificationModel::class.java
                    )
                    val response = carePointRepository.sendPushNotifications(notificationModel)
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

        //get message list
        val chatDocReference = db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        var query = chatDocReference.collection(TableName.MESSAGES)
            .orderBy("created", Query.Direction.DESCENDING)

        if (lastDocument != null)
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
    }
}
