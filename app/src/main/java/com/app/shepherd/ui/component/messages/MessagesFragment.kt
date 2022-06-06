package com.app.shepherd.ui.component.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.data.dto.login.LoginResponse
import com.app.shepherd.databinding.FragmentMessagesBinding
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.messages.adapter.DirectMessagesAdapter
import com.app.shepherd.ui.component.messages.adapter.DiscussionGroupsAdapter
import com.app.shepherd.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.app.shepherd.utils.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MessagesFragment : BaseFragment<FragmentMessagesBinding>() {

    private val messagesViewModel: MessagesViewModel by viewModels()

    private lateinit var fragmentMessagesBinding: FragmentMessagesBinding


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

        setDirectMessagesAdapter()

    }

    override fun observeViewModel() {
        observe(messagesViewModel.loginLiveData, ::handleLoginResult)
        observeEvent(messagesViewModel.openChatMessageItem, ::navigateToChatItems)
        observeSnackBarMessages(messagesViewModel.showSnackBar)
        observeToast(messagesViewModel.showToast)
    }

    private fun navigateToChatItems(navigateEvent: SingleEvent<Any>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_messages_to_chat)
        }

    }

    private fun handleLoginResult(status: Resource<LoginResponse>) {
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
        val directMessagesAdapter = DirectMessagesAdapter(messagesViewModel)
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


}

