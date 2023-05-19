package com.shepherdapp.app.ui.component.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.DeleteChat
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.chat.MessageData
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.databinding.FragmentChatBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.chat.adapter.ChatAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.ChatViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_welcome_user.*
import java.util.ArrayList
import java.util.Calendar

/**
 * Created by Nikita on 11-05-23
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener {
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var fragmentChatBinding: FragmentChatBinding
    private val TAG = "ChatFragment"
    private var userAssignes: UserAssigneDetail? = null
    private var roomId = ""
    private var allMsgLoaded: Boolean = false
    private var msgGroupList: ArrayList<MessageGroupData> = ArrayList()
    private var commentAdapter: ChatAdapter? = null
    private var unReadCount: Long = 0
    private var lastMessageSenderId = ""
    var deleteChatUserIdListing: ArrayList<DeleteChat> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentChatBinding =
            FragmentChatBinding.inflate(inflater, container, false)
        if (arguments?.containsKey("assignee_user") == true) {
            userAssignes =
                @Suppress("DEPRECATION") requireArguments().getParcelable("assignee_user")
        }
        return fragmentChatBinding.root
    }

    override fun initViewBinding() {
        fragmentChatBinding.listener = this
        roomId = if (chatViewModel.getCurrentUser()?.userId!! > userAssignes!!.id!!)
            chatViewModel.getCurrentUser()?.userId.toString() + "-" + userAssignes!!.id!!
        else
            userAssignes!!.id!!.toString() + "-" + chatViewModel.getCurrentUser()?.userId.toString()
        chatViewModel.roomId = roomId

        chatViewModel.tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        if (userAssignes != null) {
            fragmentChatBinding.imgChatUser.loadImageFromURL(
                userAssignes!!.profilePhoto,
                userAssignes!!.firstname,
                userAssignes!!.lastname
            )
            fragmentChatBinding.txtName.text =
                userAssignes!!.firstname.plus(" ${userAssignes!!.lastname}")
        }

        // Load Chat
        chatViewModel.initChatListener()
        chatViewModel.listenUnreadUpdates()

//        initScrollListener()
        setMessageAdapter()
    }

    private fun loadChat() {
        showLoading("")
        chatViewModel.getChatMessages()
            .observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {

                    when (it) {
                        is DataResult.Loading -> {
//                            showLoading("")
                        }
                        is DataResult.Failure -> {
                            hideLoading()
                            allMsgLoaded = true
                            showError(requireContext(), it.exception?.message ?: "")
                        }
                        is DataResult.Success -> {
                            hideLoading()
                            msgGroupList.clear()
                            msgGroupList.addAll(it.data.groupList)
                            msgGroupList.reverse()
                            Log.d(TAG, "loadChat: $msgGroupList")
                            getChatList()
                        }
                    }
                }
            }
    }

    private fun getChatList() {
        var chatMessages: ArrayList<MessageGroupData> = ArrayList()
        var isChatDeleted = false
        deleteChatUserIdListing.forEach { deleteChatListData ->
            if (deleteChatListData.userId?.toInt() == chatViewModel.getCurrentUser()?.userId) {
                val deletedTimeStamp = deleteChatListData.deletedAt
                isChatDeleted = true
                if (deletedTimeStamp != null) {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = deletedTimeStamp
                    msgGroupList.forEach {
                        var exist = false
                        val list: ArrayList<MessageData> = ArrayList()
                        it.messageList.forEach { messageData ->
                            if (messageData.created != null) {
                                val msgCal = Calendar.getInstance()
                                msgCal.time = messageData.created!!.toDate()
                                if (msgCal.time.after(cal.time)) {
                                    exist = true
                                    list.add(messageData)
                                }
                            }
                        }
                        if (exist) {
                            it.messageList.clear()
                            it.messageList.addAll(list)
                            chatMessages.add(it)
                        }
                    }
                }
                return@forEach
            }
        }

        if (!isChatDeleted)
            chatMessages = msgGroupList
        hideLoading()
        commentAdapter?.addData(chatMessages)
    }

    private fun setMessageAdapter() {
        //set comment adapter added in list
        fragmentChatBinding.rvMsg.setItemViewCacheSize(200)
        commentAdapter = ChatAdapter(chatViewModel)
        commentAdapter!!.setHasStableIds(true)
        fragmentChatBinding.rvMsg.adapter = commentAdapter

    }


    private fun initScrollListener() {

        fragmentChatBinding.rvMsg.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // !recyclerView.canScrollVertically(-1) returns true if top is reached
                if (!recyclerView.canScrollVertically(-1) && !allMsgLoaded) {
                    loadPreviousChat()
                }
            }
        })

    }

    private fun loadPreviousChat() {
        chatViewModel.getPreviousMessages()
    }

    override fun observeViewModel() {
        // Observe Push Notification Response
        chatViewModel.fcmResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                }
                is DataResult.Loading -> {

                }
                is DataResult.Success -> {
                    Log.d(TAG, "observeViewModel: Push Notification sent successfully...")
                }
            }
        }
        chatViewModel.lastChatDetail.observeEvent(this) {
            unReadCount = it.unseenMessageCount ?: 0
            lastMessageSenderId = it.lastSenderId!!
            deleteChatUserIdListing.clear()
            deleteChatUserIdListing.addAll(it.deletedChatUserIds)
            Log.e("catch_deleted_user", "deleteChatUserIdListing $deleteChatUserIdListing")
            loadChat()
            updateUnseenCount()
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.ivSend -> {
                val message = fragmentChatBinding.editTextMessage.text.toString().trim()
                if (message.isEmpty()) {
                    showInfo(requireContext(), "Please enter message...")
                } else {

                    unReadCount += 1
                    chatViewModel.userAssignes = userAssignes
                    chatViewModel.getAndSaveMessageData(
                        roomId,
                        Chat.MESSAGE_TEXT,
                        message = message,
                        unReadCount = unReadCount, deleteChatUserIdListing
                    )
                    fragmentChatBinding.editTextMessage.text?.clear()
                    fragmentChatBinding.rvMsg.scrollToPosition(msgGroupList.size - 1)
                    lastMessageSenderId = chatViewModel.getCurrentUser()?.userId.toString()
                    updateUnseenCount()
                }
            }
        }
    }

    private fun updateUnseenCount() {
        //clear unseen count
        if (commentAdapter != null &&
            commentAdapter?.messageList?.size!! > 0
        ) {

            if (lastMessageSenderId.toInt() != chatViewModel.getCurrentUser()?.userId) {
                //clear unseen count because the last message is from other user
                chatViewModel.updateUnseenCount(roomId, 0)
            } else {
                chatViewModel.updateUnseenCount(roomId, unReadCount)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateUnseenCount()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }

}

