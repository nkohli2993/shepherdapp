package com.shepherd.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.shepherd.app.BuildConfig
import com.shepherd.app.ShepherdApp.Companion.db
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherd.app.data.dto.chat.ChatListData
import com.shepherd.app.data.dto.chat.ChatListResponse
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.care_teams.CareTeamsRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.TableName
import com.shepherd.app.utils.extensions.createDate
import com.shepherd.app.utils.extensions.getStringDate
import com.shepherd.app.utils.showException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var TAG = "MessagesViewModel"
    private val _openChatMessage = MutableLiveData<SingleEvent<ChatListData>>()
    val openChatMessageItem: LiveData<SingleEvent<ChatListData>> get() = _openChatMessage

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponseModel>>()
    val loginLiveData: LiveData<Resource<LoginResponseModel>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData

    var chatListener: ListenerRegistration? = null
    private var lastDocument: DocumentSnapshot? = null
    private val messageList = ArrayList<ChatListData?>()
    var oldMsgList = ArrayList<ChatListData>()
    var scrollType: Boolean = false


    private var _responseLiveData = MutableLiveData<Event<DataResult<ChatListResponse>>>()
    fun getChatList(): LiveData<Event<DataResult<ChatListResponse>>> = _responseLiveData


    fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response =
                lovedOneUUID?.let {
                    careTeamsRepository.getCareTeamsByLovedOneId(
                        pageNumber, limit, status,
                        it
                    )
                }
            withContext(Dispatchers.Main) {
                response?.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }


    fun openChat(item: ChatListData) {
        _openChatMessage.value = SingleEvent(item)
    }

    fun openGroupChat(){

    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }


    private fun showLog(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e("CHAT_DATA", msg)
        }
    }

    // Search Chat
    fun searchChat(textToSearch: String, isDirectMessage: Boolean) {
        val loggedInUser = userRepository.getCurrentUser()
        val list = ArrayList<ChatListData>()
        // Searching in One to One Chat
        if (isDirectMessage) {
            oldMsgList.forEach { chatListData ->
                val data = chatListData.usersDataMap.filter {
                    it.value?.id != loggedInUser?.id.toString()
                }.map {
                    it.value
                }
                // Search is based on name
                if (data[0]?.name?.lowercase(Locale.getDefault())
                        ?.contains(textToSearch.lowercase(Locale.getDefault())) == true
                ) {
                    list.add(chatListData)
                }
            }
            messageList.clear()
            if (!textToSearch.isNullOrEmpty()) {
                if (!list.isNullOrEmpty()) {
                    messageList.addAll(list.clone() as ArrayList<ChatListData>)
                }
            } else messageList.addAll(oldMsgList)
            val chatResponse = ChatListResponse(messageList, false)
            _responseLiveData.postValue(Event(DataResult.Success(chatResponse)))

        } else {
            // Searching in Group Chat based on group name
            oldMsgList.forEach {
                if (it.groupName?.lowercase(Locale.getDefault())
                        ?.contains(textToSearch.lowercase(Locale.getDefault())) == true
                ) {
                    list.add(it)
                }
            }
            messageList.clear()
            if (!textToSearch.isNullOrEmpty()) {
                if (!list.isNullOrEmpty()) {
                    messageList.addAll(list.clone() as ArrayList<ChatListData>)
                }
            } else messageList.addAll(oldMsgList)
            val chatResponse = ChatListResponse(messageList, false)
            _responseLiveData.postValue(Event(DataResult.Success(chatResponse)))

        }
    }

    // Get One to One Chat messages
    fun getChats() {
        messageList.clear()
        Log.d(TAG, "Message List :${messageList.size} ")
        val loggedInUserID = userRepository.getCurrentUser()?.id.toString()

        val query = db.collection(TableName.CHATS)
            .whereArrayContains("userIDs", loggedInUserID ?: "")
//            .whereEqualTo("chat_type", Chat.CHAT_SINGLE)
            .orderBy("updated_at", Query.Direction.DESCENDING)

        chatListener?.remove()
        chatListener = null

        chatListener = query.addSnapshotListener { value, error ->
            if (error != null) {
                showLog(msg = "get message list failure error >> ${error.message}")
                _responseLiveData.postValue(
                    Event(
                        DataResult.Failure(
                            error.message,
                            exception = error
                        )
                    )
                )
                return@addSnapshotListener
            }

            showLog(msg = "messageList { $loggedInUserID }>> ${value!!.documentChanges.size}")
            if (!value.documentChanges.isNullOrEmpty()) {
                if (lastDocument == null) {
                    lastDocument =
                        value.documentChanges[value.documentChanges.size - 1].document

                }
            }
            for (document in value.documentChanges) {

                when (document.type) {
                    DocumentChange.Type.ADDED -> {

                        showLog("New message: ${document.document.data}")
                        try {
                            val messageModel = getMessageModel(document)
                            messageModel?.let {
                                messageList.add(it)
                            }

                        } catch (e: JSONException) {
                            showException(e)
                        }
                    }
                    DocumentChange.Type.MODIFIED -> {
                        showLog("modified message: ${document.document.data}")
                        try {


                            val messageModel = getMessageModel(document)

                            val position =
                                messageList.indexOfFirst { it?.id == messageModel?.id }
                            if (position >= 0) {
                                messageList.removeAt(position)
                            }
                            messageModel?.let {
                                messageList.add(0, it)
                            }

                        } catch (e: JSONException) {
                            showException(e)
                        }

                    }
                }
            }
            /* if (!userIdList.isNullOrEmpty() && userIdList.size < 10) {
                 getUsers(userIdList.clone() as ArrayList<String>)
                 userIdList.clear()
             }*/

            sortMessages(true)

        }

    }

    private fun sortMessages(type: Boolean) {

        messageList.sortByDescending { it?.date?.createDate("yyyy-MM-dd HH:mm:ss")?.time }
        oldMsgList.clear()
        if (!messageList.isNullOrEmpty()) {
//            oldMsgList.addAll(messageList.toList() as ArrayList<ChatListData>)
            messageList.forEach {
                it?.copy()?.let { it1 -> oldMsgList.add(it1) }
            }
        }

        scrollType = type
        val chatResponse = ChatListResponse(messageList, type)
        _responseLiveData.postValue(Event(DataResult.Success(chatResponse)))
    }

    private fun getMessageModel(document: DocumentChange): ChatListData? {
        val messageModel = Gson().fromJson(
            JSONObject(document.document.data).toString(),
            ChatListData::class.java
        )
        messageModel.updated_at =
            (document.document.data["updated_at"] as Timestamp?)
        val cal = Calendar.getInstance().apply {
            if (messageModel.updated_at != null)
                time = messageModel.updated_at?.toDate()
        }
        messageModel.date = cal.time.getStringDate("yyyy-MM-dd HH:mm:ss")


//        if (messageModel.senderId?.equals(userData?._id) == true) {
//            messageModel.unread_count = 0
//        }
//

        messageModel.usersDataMap.entries.forEach {
            /* if (!it.key.equals(
                     userRepository.getCurrentUser()?.id
                 ) && messageModel.chatType == Chat.CHAT_SINGLE
             ) {
                 val existingData =
                     userList.find { listData -> listData.getLoggedInUserId().equals(it.key) }
                 if (existingData == null) {
                     userIdList.add(it.key)
                     messageModel.toUser = it.value
                 } else {
                     messageModel.toUser = existingData.getChatUser()
                 }


             }*/

            if (it.key.equals(userRepository.getCurrentUser()?.id)) {
                messageModel.unreadCount = it.value?.unreadCount

            }
            //            messageModel.userIDs?.find { !it.equals(userData?.user_id) }?.let { userId ->
            //                if (userList.filter { it.user_id.equals(userId) }.isNullOrEmpty()) {
            //
            //                    userIdList.add(userId)
            //                }
            //
            //            }
        }
        /* if (userIdList.size == 10) {
             getUsers(userIdList.clone() as ArrayList<String>)
             userIdList.clear()
         }*/
        return messageModel
    }

}
