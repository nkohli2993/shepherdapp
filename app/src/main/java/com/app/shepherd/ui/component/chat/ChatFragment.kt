package com.app.shepherd.ui.component.chat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentChatBinding
import com.app.shepherd.databinding.FragmentMessagesBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.chat.adapter.ChatAdapter
import com.app.shepherd.ui.component.home.HomeActivity
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentMessagesBinding>(),
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
        observeSnackBarMessages(chatViewModel.showSnackBar)
        observeToast(chatViewModel.showToast)
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

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentChatBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentChatBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setChatAdapter() {
        val chatAdapter = ChatAdapter(chatViewModel)
        fragmentChatBinding.recyclerViewChat.adapter = chatAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.buttonSubmit,R.id.imageViewBack -> {
               //backPress()
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            }
        }
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_chat
    }


}

