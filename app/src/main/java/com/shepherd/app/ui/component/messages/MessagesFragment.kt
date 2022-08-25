package com.shepherd.app.ui.component.messages

import android.os.Bundle
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
import com.shepherd.app.utils.*
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
        messagesViewModel.getOneToOneChats()
        setDirectMessagesAdapter()
    }

    override fun observeViewModel() {
        observe(messagesViewModel.loginLiveData, ::handleLoginResult)
        observeEvent(messagesViewModel.openChatMessageItem, ::navigateToChatItems)
        observeSnackBarMessages(messagesViewModel.showSnackBar)
        observeToast(messagesViewModel.showToast)
        // Observe One To One Chat Data
        messagesViewModel.getOneToOneChatList().observeEvent(this) {
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
                    Log.d(TAG, "One To One Chat Data :$data ")
                    directMessagesAdapter?.addData(data)
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

//    private fun setDiscussionsGroupAdapter() {
//        val discussionsGroupAdapter = DiscussionGroupsAdapter(messagesViewModel)
//        fragmentMessagesBinding.recyclerViewDiscussionGroups.adapter = discussionsGroupAdapter
//
//        fragmentMessagesBinding.recyclerViewDiscussionGroups.addItemDecoration(
//            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        )
//    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_messages
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.txtDirectMessages -> {
                Log.d(TAG, "onClick: Direct message clicked")
                //Get One to One Chat Data
                messagesViewModel.getOneToOneChats()
            }
            R.id.txtDiscussionGroups -> {

            }
        }
    }


}

