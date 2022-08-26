package com.shepherd.app.ui.component.chat

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.shepherd.app.R
import com.shepherd.app.data.dto.chat.ChatModel
import com.shepherd.app.data.dto.chat.ChatUserDetail
import com.shepherd.app.data.dto.chat.MessageGroupData
import com.shepherd.app.databinding.FragmentChatBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.chat.adapter.ChatAdapter
import com.shepherd.app.ui.component.chat.adapter.ChatGroupAdapter
import com.shepherd.app.utils.Chat
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.ChatViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(),
    View.OnClickListener {

    private val chatViewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private var chatModel: ChatModel? = null
    private var chatModelList: Array<ChatModel>? = null
    private var chatUserDetailList: ArrayList<ChatUserDetail>? = ArrayList()
    private var allMsgLoaded: Boolean = false
    private var msgGroupList: ArrayList<MessageGroupData> = ArrayList()
    private var chatAdapter: ChatAdapter? = null
    private var adapter: ChatGroupAdapter? = null

    var chatType: Int? = null
    var groupName: String? = null


    private lateinit var fragmentChatBinding: FragmentChatBinding
    private val TAG = "ChatFragment"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentChatBinding =
            FragmentChatBinding.inflate(inflater, container, false)

        return fragmentChatBinding.root
    }

    override fun initViewBinding() {
        fragmentChatBinding.listener = this
        val source = args.source
        Log.d(TAG, "Source : $source ")

//        chatModel = args.chatModel
        chatModelList = args.chatModel
        Log.d(TAG, "Chat ModelList : $chatModelList ")

        chatType = chatModelList?.get(0)?.chatType
        Log.d(TAG, "ChatType :$chatType ")
        // Check if One to one or Group Chat
        if (chatType == Chat.CHAT_SINGLE) {
            fragmentChatBinding.llImageWrapper.visibility = View.VISIBLE
            // Set Name of the Chat User
            fragmentChatBinding.txtName.text = chatModelList?.get(0)?.receiverName
            // Set Profile Pic
            if (!chatModelList?.get(0)?.receiverPicUrl.isNullOrEmpty()) {
                Picasso.get().load(chatModelList?.get(0)?.receiverPicUrl)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(fragmentChatBinding.imgChatUser)
            }

        } else if (chatType == Chat.CHAT_GROUP) {
            fragmentChatBinding.txtName.text = chatModelList?.get(0)?.groupName
            fragmentChatBinding.llImageWrapper.visibility = View.GONE
        }

        val count = chatModelList?.filter {
            it.chatType == Chat.CHAT_SINGLE
        }?.count()

        if (count != null) {
            if (count > 0) {
                fragmentChatBinding.data = chatModelList?.get(0)
            }
        }

        val countGroup = chatModelList?.filter {
            it.chatType == Chat.CHAT_GROUP
        }?.count()

        if (countGroup != null) {
            if (countGroup > 0) {
                fragmentChatBinding.txtName.text = chatModelList?.get(0)?.groupName
            }
        }

//        fragmentChatBinding.data = chatModel
        chatModelList?.forEach {
            val chatUserDetail = it.toChatUserDetail()
            chatUserDetailList?.add(chatUserDetail)
        }


        // Set User Details
//        chatViewModel.setToUserDetail(chatModel?.toChatUserDetail())
        chatViewModel.setToUserDetail(chatType, chatUserDetailList)
        loadChat()
//        setChatAdapter()
        setGroupChatAdapter()
    }

    private fun setGroupChatAdapter() {
        adapter = ChatGroupAdapter(chatViewModel)
        fragmentChatBinding.rvMsg.adapter = adapter
    }


    private fun loadChat() {
        chatViewModel.getChatMessages()
            .observe(viewLifecycleOwner) { event ->
                event.getContentIfNotHandled()?.let {

                    when (it) {
                        is DataResult.Loading -> {
                            showLoading("")
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
                            Log.d(TAG, "loadChat: $msgGroupList")
                            /* chatAdapter?.addData(
                                 msgGroupList,
                                 chatViewModel.loggedInUser.id.toString()
                             )*/
                            setAdapter(it.data.scrollToBottom ?: false)
                        }
                    }
                }
            }
    }

    private fun setAdapter(scrollToBottom: Boolean) {
        adapter?.addData(msgGroupList)

        if (scrollToBottom) {
            Handler().postDelayed({
                (fragmentChatBinding.rvMsg.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                    0,
                    0
                )

            }, 200)
        }
    }

    private fun ChatModel.toChatUserDetail(): ChatUserDetail {
        return ChatUserDetail(
            id = this.receiverID.toString() ?: "",
            name = this.receiverName ?: "",
            imageUrl = this.receiverPicUrl ?: ""
        )
    }

    override fun observeViewModel() {

        chatViewModel.noChatDataFoundLiveData.observeEvent(this) {
            if (it) {
                Log.d(TAG, "observeViewModel: No Chat Data Found")

                // Check if chat room is opened for group chat
                val countGroup = chatModelList?.filter { chatModel ->
                    chatModel.chatType == Chat.CHAT_GROUP
                }?.count()

                if (countGroup != null) {
                    if (countGroup > 0) {
                        showEnterGroupNameDialog()
                    }
                }
            }
        }

        chatViewModel.groupNameLiveData.observeEvent(this) {
            fragmentChatBinding.txtName.text = it
            fragmentChatBinding.llImageWrapper.visibility = View.GONE
        }
    }

    private fun showEnterGroupNameDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enter_group_name)
        val edtGroupName = dialog.findViewById(R.id.edtGroupName) as EditText
        val btnOkay = dialog.findViewById(R.id.btnOkay) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView
        btnOkay.setOnClickListener {
            groupName = edtGroupName.text.toString().trim()
            chatViewModel.chatListData?.groupName = groupName
            fragmentChatBinding.txtName.text = groupName
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            /*  R.id.buttonSubmit,R.id.imageViewBack -> {
                 //backPress()
                  startActivity(Intent(requireContext(), HomeActivity::class.java))
              }*/
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.ivSend -> {
                val message = fragmentChatBinding.edtMessage.text.toString().trim()
                if (message.isNullOrEmpty()) {
                    showInfo(requireContext(), "Please enter message...")
                } else {
                    chatModel?.chatType = chatType
                    chatModel?.message = message
                    Log.d(TAG, "Send Message :$chatModel ")
//                    chatModel?.let { chatViewModel.sendMessage(it) }
                    chatViewModel.getAndSaveMessageData(Chat.MESSAGE_TEXT, message = message)
                    fragmentChatBinding.edtMessage.text?.clear()
                }
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }


}

