package com.shepherdapp.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.ShepherdApp.Companion.db
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.DeleteChat
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.chat.ChatListData
import com.shepherdapp.app.data.dto.chat.ChatListResponse
import com.shepherdapp.app.data.dto.chat.ChatUserListing
import com.shepherdapp.app.data.dto.dashboard.LoveUser
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.utils.extensions.createDate
import com.shepherdapp.app.utils.extensions.getStringDate
import com.shepherdapp.app.utils.showException
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
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {
    var tableName: String? = null
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
    private val messageList = ArrayList<ChatUserListing?>()
    var oldMsgList = ArrayList<ChatListData>()

//    fun getLovedOneId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_ID)
    fun getLovedUserDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }

    fun getLovedUser(): LoveUser? {
        return Prefs.with(ShepherdApp.appContext)!!.getObject(
            Const.LOVED_USER_DETAILS,
            LoveUser::class.java
        )
    }


    private var _searchCareTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var searchCareTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _searchCareTeamsResponseLiveData

    private var _responseLiveData = MutableLiveData<Event<DataResult<ChatUserListing>>>()
    fun getChatList(): LiveData<Event<DataResult<ChatUserListing>>> = _responseLiveData

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getCurrentUser(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun openChat(item: ChatListData) {
        _openChatMessage.value = SingleEvent(item)
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }


    private fun showLog(msg: String) {
        Log.e("CHAT_DATA", msg)
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
        }
    }

    fun getChatRooms(
        userId: Int, loveOneId: Int,
        onListen: (ArrayList<ChatUserListing>) -> Unit
    ): ListenerRegistration {
        var opponentUserIdList: ArrayList<ChatUserListing> = ArrayList()
        return db.collection(tableName!!)
            //.whereArrayContains("userIds", FirebaseAuth.getInstance().currentUser?.uid!!)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    Log.e("FIRESTORE", "Users listener error.", firebaseFirestoreException)
                    return@addSnapshotListener
                }
                opponentUserIdList = ArrayList()
                querySnapshot?.documents?.forEach {
                    val userIds = it.id.split("-")
                    Log.e("catch_dat", "value: $userIds $userId $loveOneId")
//                    userIds.forEachIndexed { index, s ->
//
//                    }
                    if (it.id.contains(userId.toString()) && it.id.contains(loveOneId.toString())) {
//                        if (s.contains(usersList.joinToString().replace(" ","").replace(",","-"))) {
                        val users = it.toObject(ChatUserListing::class.java)
                        opponentUserIdList.add(users!!)
                    }
                }
                onListen(opponentUserIdList)
            }
    }

    fun deleteChat(roomId:String,deleteChatArrayList:ArrayList<DeleteChat>){
        db.collection(tableName!!).document(roomId)
            .set(hashMapOf("deletedChatUserIds" to deleteChatArrayList), SetOptions.merge())
    }

    fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
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



    // Get One to One Chat messages   for discussion
    fun getChats() {
        messageList.clear()
        Log.d(TAG, "Message List :${messageList.size} ")
        val loggedInUserID = userRepository.getCurrentUser()?.userId.toString()

        val query = db.collection(tableName!!)
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
                            messageModel?.let {
                                messageList.add(0, it)
                            }

                        } catch (e: JSONException) {
                            showException(e)
                        }

                    }
                    else -> {}
                }
            }
        }

    }
    private fun getMessageModel(document: DocumentChange): ChatUserListing? {
        Log.e("catch_exception","document: ${document}")
        val messageModel = Gson().fromJson(
            document.document.toString(),
            ChatUserListing::class.java
        )
        return messageModel
    }
//*************************************

    fun searchCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int,
        search: String
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response =
                lovedOneUUID?.let {
                    careTeamsRepository.searchCareTeamsByLovedOneId(
                        pageNumber, limit, status,
                        it, search
                    )
                }
            withContext(Dispatchers.Main) {
                response?.collect { _searchCareTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return searchCareTeamsResponseLiveData
    }


}
