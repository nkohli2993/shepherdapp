package com.shepherd.app.ui.component.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.chat.ChatModel
import com.shepherd.app.databinding.FragmentChatBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.chat.adapter.ChatAdapter
import com.shepherd.app.utils.extensions.showInfo
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
        chatModel = args.chatModel
        Log.d(TAG, "Chat Model : $chatModel ")

        fragmentChatBinding.data = chatModel
        setChatAdapter()
    }

    override fun observeViewModel() {
    }


    private fun setChatAdapter() {
        val chatAdapter = ChatAdapter(chatViewModel)
        fragmentChatBinding.recyclerViewChat.adapter = chatAdapter
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
                if(message.isNullOrEmpty()){
                    showInfo(requireContext(),"Please enter message...")
                }else{
                    chatModel?.message = message
                    Log.d(TAG, "Send Message :$chatModel ")
                }

            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }


}

