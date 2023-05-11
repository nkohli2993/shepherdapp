package com.shepherdapp.app.ui.component.chat

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
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
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.data.dto.chat.ChatModel
import com.shepherdapp.app.data.dto.chat.ChatUserDetail
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.data.dto.chat.MessageGroupResponse
import com.shepherdapp.app.databinding.FragmentChatBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.chat.adapter.ChatAdapter
import com.shepherdapp.app.ui.component.chat.adapter.ChatGroupAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.ChatViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList


/**
 * Created by Nikita on 11-05-23
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(), View.OnClickListener {
    private val chatViewModel: ChatViewModel by viewModels()
    private var chatModel: ChatModel? = null
    private lateinit var fragmentChatBinding: FragmentChatBinding
    private val TAG = "ChatFragment"
    private var userAssignes: UserAssigneeModel? = null
    private var chatUserDetailList: ArrayList<ChatUserDetail>? = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentChatBinding =
            FragmentChatBinding.inflate(inflater, container, false)
        if (arguments?.containsKey("assignee_user") == true) {
            userAssignes = requireArguments().getParcelable("assignee_user")
        }

        return fragmentChatBinding.root
    }

    override fun initViewBinding() {
        fragmentChatBinding.listener = this
        if (userAssignes != null) {
            fragmentChatBinding.imgChatUser.loadImageFromURL(
                userAssignes!!.user_details.profilePhoto,
                userAssignes!!.user_details.firstname,
                userAssignes!!.user_details.lastname
            )
            fragmentChatBinding.txtName.text =
                userAssignes!!.user_details.firstname.plus(" ${userAssignes!!.user_details.lastname}")
        }

        chatUserDetailList!!.add(ChatUserDetail(id=userAssignes!!.user_details.id.toString(), imageUrl = userAssignes!!.user_details.profilePhoto?:"",0,userAssignes!!.user_details.firstname.plus(" ${userAssignes!!.user_details.lastname}")))
        chatViewModel.setToUserDetail(
            Chat.CHAT_SINGLE,
            chatUserDetailList
        )
        // Load Chat
//        loadChat()
//        initScrollListener()
    }

    override fun observeViewModel() {

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.ivSend -> {
                val message = fragmentChatBinding.editTextMessage.text.toString().trim()
                if (message.isNullOrEmpty()) {
                    showInfo(requireContext(), "Please enter message...")
                } else {
                    chatModel?.chatType = Chat.CHAT_GROUP
                    chatModel?.message = message
                    Log.d(TAG, "Send Message :$chatModel ")
//                    chatModel?.let { chatViewModel.sendMessage(it) }
                    chatViewModel.getAndSaveMessageData(Chat.MESSAGE_TEXT, message = message)
                    fragmentChatBinding.editTextMessage.text?.clear()
                    hideKeyboard()
                }
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }

}

