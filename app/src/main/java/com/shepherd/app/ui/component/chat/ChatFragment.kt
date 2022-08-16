package com.shepherd.app.ui.component.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentChatBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.chat.adapter.ChatAdapter
import com.shepherd.app.utils.observe
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(),
    View.OnClickListener {

    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var fragmentChatBinding: FragmentChatBinding


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

        setChatAdapter()

    }

    override fun observeViewModel() {
        observe(chatViewModel.loginLiveData, ::handleLoginResult)
    }


    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { chatViewModel.showToastMessage(it) }
            }
        }
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
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }


}

