package com.shepherd.app.ui.component.messages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentMessagesBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.messages.adapter.DirectMessagesAdapter
import com.shepherd.app.ui.component.messages.adapter.DiscussionGroupsAdapter
import com.shepherd.app.utils.*
import com.shepherd.app.utils.extensions.hideKeyboard
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding>(), View.OnClickListener {

    private val messagesViewModel: MessagesViewModel by viewModels()
    private lateinit var fragmentMessagesBinding: FragmentMessagesBinding
    private var TAG = "MessagesFragment"
    private var directMessagesAdapter: DirectMessagesAdapter? = null
    private var discussionsGroupAdapter: DiscussionGroupsAdapter? = null

    // keep track of Direct message or Discussions group
    // true means  Direct Message and false means discussion group
    private var isDirectMessage: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMessagesBinding =
            FragmentMessagesBinding.inflate(inflater, container, false)

        return fragmentMessagesBinding.root
    }

    override fun initViewBinding() {
        fragmentMessagesBinding.listener = this
        //Get One to One Chat Data
        messagesViewModel.getChats()
        fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.GONE
        fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.VISIBLE
        setDirectMessagesAdapter()
        setDiscussionsGroupAdapter()

        // By default, one to one messages will be shown
        isDirectMessage = true

        // Search functionality
        fragmentMessagesBinding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                messagesViewModel.searchChat(p0.toString(), isDirectMessage!!)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        fragmentMessagesBinding.llSearch.setOnClickListener {
            fragmentMessagesBinding.editTextSearch.text?.clear()
            if (isDirectMessage == true) {
                fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.GONE
                fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.VISIBLE
                messagesViewModel.getChats()
            } else {
                fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.VISIBLE
                fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.GONE
                messagesViewModel.getChats()
            }
        }
    }

    override fun observeViewModel() {
        observe(messagesViewModel.loginLiveData, ::handleLoginResult)
        observeEvent(messagesViewModel.openChatMessageItem, ::navigateToChatItems)
        observeSnackBarMessages(messagesViewModel.showSnackBar)
        observeToast(messagesViewModel.showToast)
        // Observe One To One Chat Data
        messagesViewModel.getChatList().observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val data = it.data.list
                    if (data.isNullOrEmpty()) {
                        hideKeyboard()
                        fragmentMessagesBinding.txtNoChat.visibility = View.VISIBLE
                        fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.GONE
                        fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.GONE
                    } else {
                        fragmentMessagesBinding.txtNoChat.visibility = View.GONE
                        Log.d(TAG, "Chat Data :$data ")
                        val oneToOneChatData = data.filter {
                            it?.chatType == Chat.CHAT_SINGLE
                        } as ArrayList
                        Log.d(TAG, "One to One Chat Data :$oneToOneChatData ")

                        if (!oneToOneChatData.isNullOrEmpty())
                            directMessagesAdapter?.addData(oneToOneChatData)

                        val groupChatData = data.filter {
                            it?.chatType == Chat.CHAT_GROUP
                        } as ArrayList
                        Log.d(TAG, "Group Chat Data :$groupChatData ")

                        if (!groupChatData.isNullOrEmpty())
                            discussionsGroupAdapter?.addData(groupChatData)
                        if (isDirectMessage == true) {
                            fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.GONE
                            fragmentMessagesBinding.recyclerViewDirectMessages.visibility =
                                View.VISIBLE
                        } else {
                            fragmentMessagesBinding.rvDiscussionGroupMessages.visibility =
                                View.VISIBLE
                            fragmentMessagesBinding.recyclerViewDirectMessages.visibility =
                                View.GONE
                        }
                    }


                }
            }
        }
    }

    private fun navigateToChatItems(navigateEvent: SingleEvent<Any>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_messages_to_chat)
        }

    }

    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { messagesViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentMessagesBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentMessagesBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }


    private fun setDirectMessagesAdapter() {
        directMessagesAdapter = DirectMessagesAdapter(messagesViewModel)
        fragmentMessagesBinding.recyclerViewDirectMessages.adapter = directMessagesAdapter

//        fragmentMessagesBinding.recyclerViewDirectMessages.addItemDecoration(
//            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        )


    }

    private fun setDiscussionsGroupAdapter() {
        discussionsGroupAdapter = DiscussionGroupsAdapter(messagesViewModel)
        fragmentMessagesBinding.rvDiscussionGroupMessages.adapter = discussionsGroupAdapter
//        fragmentMessagesBinding.recyclerViewDiscussionGroups.addItemDecoration(
//            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        )
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_messages
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtDirectMessages -> {
                Log.d(TAG, "onClick: Direct message clicked")
                isDirectMessage = true
                fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.GONE
                fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.VISIBLE
                messagesViewModel.getChats()
            }
            R.id.txtDiscussionGroups -> {
                isDirectMessage = false
                fragmentMessagesBinding.rvDiscussionGroupMessages.visibility = View.VISIBLE
                fragmentMessagesBinding.recyclerViewDirectMessages.visibility = View.GONE
                messagesViewModel.getChats()
            }
        }
    }


}

