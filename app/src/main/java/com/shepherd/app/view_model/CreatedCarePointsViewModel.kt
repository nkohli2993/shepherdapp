package com.shepherd.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.shepherd.app.BuildConfig
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.added_events.*
import com.shepherd.app.data.dto.chat.*
import com.shepherd.app.data.dto.dashboard.LoveUser
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.care_point.CarePointRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private var chatResponseData = MutableLiveData<Event<DataResult<MessageGroupResponse>>>()
    fun getChatMessages(): LiveData<Event<DataResult<MessageGroupResponse>>> = chatResponseData

    private var _groupNameLiveData = MutableLiveData<Event<String>>()
    var groupNameLiveData: LiveData<Event<String>> = _groupNameLiveData

    private var _noChatDataFoundLiveData = MutableLiveData<Event<Boolean>>()
    var noChatDataFoundLiveData: LiveData<Event<Boolean>> = _noChatDataFoundLiveData


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


    fun setToUserDetail(chatType: Int?, toUsers: ArrayList<ChatUserDetail>?, groupName: String?) {
        this.groupName = groupName
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
        chatListData = createChatListData(chatType, memberList, groupName)
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

        ShepherdApp.db.collection(TableName.CHATS)
//            .whereEqualTo("userIDs", userIDs)
            .whereEqualTo("group_name", groupName)
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {

                    // Get the document id of the messages
                    chatListData?.id = it.documents[0].id

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
                    if (isFirstTime) {
                        findChatId(isFirstTime = false) { isFounded ->
                            onFound(isFounded)
                        }
                    } else {
                        onFound(false)
                        _noChatDataFoundLiveData.postValue(Event(true))
                    }
                }
            }.addOnFailureListener {
                if (BuildConfig.DEBUG) {
                    it.printStackTrace()
                }
                onFound(false)
            }
    }

    private fun initChatListener() {
        isListenerInitialized = true
        chatResponseData.postValue(Event(DataResult.Loading()))
        val chatDocReference =
            ShepherdApp.db.collection(TableName.CHATS).document(chatListData?.id ?: "")

        var query = chatDocReference.collection(TableName.MESSAGES)
            .orderBy("created", Query.Direction.DESCENDING)
        query = query.limit(30)

        messageListener?.remove()
        messageListener = null
        listenUnreadUpdates()

        messageListener = query.addSnapshotListener { snapshot, e ->
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
        val chatRef = ShepherdApp.db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        chatListener?.remove()
        chatListener = null
        chatListener = chatRef.addSnapshotListener { value, error ->
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
                        ShepherdApp.db.collection(TableName.CHATS).document(chatListData?.id ?: "")
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
        groupName: String?
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

                    /* Prefs.with(CheckmateForeverApp.appContext)!!.save(
                         Const.CHAT_ID,
                         chatListData?.id
                     )*/
                    initChatListener()
                    onChatCreated(true)


                }
        } else {
            ShepherdApp.db.collection(TableName.CHATS).document(chatListData?.id ?: "")
                .set(chatListData.serializeToMap())
                .addOnSuccessListener {
                    initChatListener()
                    onChatCreated(true)
                }
        }

    }

    private fun addMessageInDb(messageData: MessageData) {
        val chatReference =
            ShepherdApp.db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        chatReference.collection(TableName.MESSAGES)
            .add(messageData.serializeToMap()).addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )
                updateUnreadCount(chatReference, messageData, false)
//                sendNotification(messageData)
            }
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
}
