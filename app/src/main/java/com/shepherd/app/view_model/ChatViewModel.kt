package com.shepherd.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.shepherd.app.BuildConfig
import com.shepherd.app.ShepherdApp.Companion.db
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.chat.*
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {
    var chatModel: ChatModel? = null
    var chatListData: ChatListData? = null
    var isListenerInitialized: Boolean = false
    private var lastDocument: DocumentSnapshot? = null
    private var allMsgList: ArrayList<MessageData> = ArrayList()
    var loggedInUser = userRepository.getCurrentUser()

    private var chatResponseData = MutableLiveData<Event<DataResult<MessageGroupResponse>>>()
    fun getChatMessages(): LiveData<Event<DataResult<MessageGroupResponse>>> = chatResponseData

    private var _noChatDataFoundLiveData = MutableLiveData<Event<Boolean>>()
    var noChatDataFoundLiveData: LiveData<Event<Boolean>> = _noChatDataFoundLiveData

    private var _groupNameLiveData = MutableLiveData<Event<String>>()
    var groupNameLiveData: LiveData<Event<String>> = _groupNameLiveData

    var messageListener: ListenerRegistration? = null
    var chatListener: ListenerRegistration? = null
    private var TAG = "chat_data"


    fun UserProfile.toChatUser(): ChatUserDetail {
        return ChatUserDetail().apply {
            id = this@toChatUser.id.toString()
            name = this@toChatUser.firstname + " " + this@toChatUser.lastname
            imageUrl = this@toChatUser.profilePhoto ?: ""
        }
    }


    /* fun setToUserDetail(toUser: ChatUserDetail?) {
         val memberList = ArrayList<ChatUserDetail?>()
         memberList.add(toUser)
         val loggedInChatUser = loggedInUser?.toChatUser()
         memberList.add(loggedInChatUser)
         chatListData = createChatListData(memberList).apply {
             chatType = Chat.CHAT_SINGLE
         }
         findChatId()
     }*/

    fun setToUserDetail(chatType: Int?, toUsers: ArrayList<ChatUserDetail>?) {
        val memberList = ArrayList<ChatUserDetail?>()
        toUsers?.let { memberList.addAll(it) }
        val loggedInChatUser = loggedInUser?.toChatUser()
        memberList.add(loggedInChatUser)

        /* if (chatType == Chat.CHAT_SINGLE) {
             chatListData = createChatListData(memberList).apply {
                 chatType = Chat.CHAT_SINGLE
             }
         } else if (chatType == Chat.CHAT_GROUP) {
             chatListData = createChatListData(memberList).apply {
                 chatType = Chat.CHAT_GROUP
             }
         }*/

        chatListData = createChatListData(chatType, memberList)
        Log.d(TAG, "setToUserDetail: ChatListData : $chatListData")

        /* chatListData = createChatListData(memberList).apply {
             chatType = Chat.CHAT_SINGLE
         }*/
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

        db.collection(TableName.CHATS).whereEqualTo("userIDs", userIDs)
//            .whereEqualTo("chat_type", Chat.CHAT_SINGLE)
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {

                    // Get the document id of the messages
                    chatListData?.id = it.documents[0].id
                    /* Prefs.with(CheckmateForeverApp.appContext)!!.save(
                         Const.CHAT_ID, chatListData?.id
                     )*/
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
//                    showLog(TAG, "id>>> ${it.documents[0].id}")
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

    fun createChatListData(chatType: Int?, userList: ArrayList<ChatUserDetail?>?): ChatListData {
        return ChatListData().apply {
            userIDs = ArrayList<String>()
            usersDataMap = HashMap()
            userList?.forEach {
                userIDs?.add(it?.id ?: "")
                usersDataMap.put(it?.id ?: "", it)
            }
            this.chatType = chatType
        }
    }

    fun getAndSaveMessageData(msgType: Int, imageFile: String = "", message: String? = "") {
        val data = MessageData().apply {
            content = message
            isRead = false
            senderID = userRepository.getCurrentUser()?.id.toString()
            messageType = msgType
            readIds = ArrayList<String>().apply {
                add(userRepository.getCurrentUser()?.id.toString() ?: "")
            }
            senderName =
                userRepository.getCurrentUser()?.firstname + " " + userRepository.getCurrentUser()?.lastname
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

    private fun addMessageInDb(messageData: MessageData) {
        val chatReference = db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        chatReference.collection(TableName.MESSAGES)
            .add(messageData.serializeToMap()).addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )
//                updateUnreadCount(chatReference, messageData, false)
//                sendNotification(messageData)
            }
    }

    /* fun sendMessage(chatModel: ChatModel) {
         this.chatModel = chatModel
         if (isListenerInitialized) {
             addMessageInDb(chatModel)
         } else {
             createNewChat {
                 if (it) {
                     addMessageInDb(chatModel)
                 }
             }
         }
     }*/

    /*private fun addMessageInDb(chatModel: ChatModel) {
        val chatReference = db.collection(TableName.CHATS).document(chatModel.id ?: "")
        chatReference.collection(TableName.MESSAGES)
            .add(chatModel.serializeToMap()).addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )
            }
    }*/

    /* private fun createNewChat(onChatCreated: (created: Boolean) -> Unit) {
         if (chatModel?.id.isNullOrEmpty()) {
             db.collection(TableName.CHATS).add(chatModel.serializeToMap())
                 .addOnSuccessListener {

                     db.collection(TableName.CHATS).document(it.id).update("id", it.id)
 //                    chatModel?.id = it.id
                     chatListData?.id = it.id
                     initChatListener()
                     onChatCreated(true)
                 }
         } else {
             db.collection(TableName.CHATS).document(chatModel?.id ?: "")
                 .set(chatModel.serializeToMap())
                 .addOnSuccessListener {
                     initChatListener()
                     onChatCreated(true)
                 }
         }

     }*/

    fun createNewChat(onChatCreated: (created: Boolean) -> Unit) {
        if (chatListData?.id.isNullOrEmpty()) {
            db.collection(TableName.CHATS).add(chatListData.serializeToMap())
                .addOnSuccessListener {

                    db.collection(TableName.CHATS).document(it.id).update("id", it.id)
                    chatListData?.id = it.id

                    /* Prefs.with(CheckmateForeverApp.appContext)!!.save(
                         Const.CHAT_ID,
                         chatListData?.id
                     )*/
                    initChatListener()
                    onChatCreated(true)


                }
        } else {
            db.collection(TableName.CHATS).document(chatListData?.id ?: "")
                .set(chatListData.serializeToMap())
                .addOnSuccessListener {
                    initChatListener()
                    onChatCreated(true)
                }
        }

    }

    private fun initChatListener() {
        isListenerInitialized = true
        chatResponseData.postValue(Event(DataResult.Loading()))
        val chatDocReference =
            db.collection(TableName.CHATS).document(chatListData?.id ?: "")

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

    fun updateReadByData() {
        db.runTransaction { transaction ->
            allMsgList.forEach { message ->
                if (!message.readIds.contains(loggedInUser?.id.toString())) {
                    val docRef = db.collection(TableName.CHATS).document(chatListData?.id ?: "")
                        .collection(TableName.MESSAGES).document(message.id ?: "")
                    transaction.get(docRef).data?.let {
                        val messageModel = Gson().fromJson(
                            JSONObject(it).toString(),
                            MessageData::class.java
                        )
                        messageModel.readIds.add(loggedInUser?.id.toString() ?: "")
                        transaction.update(docRef, "readIds", messageModel.readIds)
                        if (messageModel.readIds.size >= chatListData?.userIDs?.size ?: 0) {
                            transaction.update(docRef, "isRead", true)

                        }
                    }

                }
            }
        }
    }

    fun listenUnreadUpdates() {
        val chatRef = db.collection(TableName.CHATS).document(chatListData?.id ?: "")
        chatListener?.remove()
        chatListener = null
        chatListener = chatRef.addSnapshotListener { value, error ->
            if (value?.metadata?.hasPendingWrites() == false) {
                if (value.data != null) {
                    val chatData = Gson().fromJson(
                        JSONObject(value.data!!).toString(),
                        ChatListData::class.java
                    )

                    if (chatData.usersDataMap[loggedInUser?.id
                            ?: ""]?.unreadCount ?: 0 > 0
                    ) {
                        chatData.usersDataMap[loggedInUser?.id
                            ?: ""]?.unreadCount = 0
                        chatRef.update("users_data", chatData.usersDataMap.serializeToMap())
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

}
