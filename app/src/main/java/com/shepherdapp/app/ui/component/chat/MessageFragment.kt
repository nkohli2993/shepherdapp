package com.shepherdapp.app.ui.component.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ListenerRegistration
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.chat.ChatListData
import com.shepherdapp.app.data.dto.chat.ChatUserListing
import com.shepherdapp.app.databinding.FragmentMessageBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.chat.adapter.MessagesListingAdapter
import com.shepherdapp.app.utils.Chat
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(), View.OnClickListener,
    MessagesListingAdapter.OnItemClickListener {
    private lateinit var fragmentMessageBinding: FragmentMessageBinding
    private var roomChatList: List<ChatUserListing> = ArrayList()
    private var searchedChatList: ArrayList<ChatUserListing> = ArrayList()
    private var messagesListingAdapter: MessagesListingAdapter? = null
    private var chatChannelRegistration: ListenerRegistration? = null
    private var isActive = false
    override fun getLayoutRes() = R.layout.fragment_message
    private val messagesViewModel: MessagesViewModel by viewModels()
    private val TAG = "MessageFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMessageBinding =
            FragmentMessageBinding.inflate(inflater, container, false)

        return fragmentMessageBinding.root
    }
    override fun initViewBinding() {
       fragmentMessageBinding.listener = this

    }

    override fun observeViewModel() {
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
                        fragmentMessageBinding.textViewNoMessages.visibility = View.VISIBLE
                        fragmentMessageBinding.rvChatListing.visibility = View.GONE
                    } else {
                        fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
                        Log.d(TAG, "Chat Data :$data ")
                        val oneToOneChatData = data.filter {
                            it?.chatType == Chat.CHAT_SINGLE
                        } as ArrayList
                        Log.d(TAG, "One to One Chat Data :$oneToOneChatData ")


                        if (!oneToOneChatData.isNullOrEmpty())
                            messagesListingAdapter?.addData(oneToOneChatData)

                        val groupChatData = data.filter {
                            it?.chatType == Chat.CHAT_GROUP
                        } as ArrayList
                        Log.d(TAG, "Group Chat Data :$groupChatData ")
                    }


                }
            }
        }

    }

    private fun updateRecyclerView() {
        try {

            if (roomChatList.isNotEmpty())
               fragmentMessageBinding.textViewNoMessages.visibility = View.GONE

            roomChatList = roomChatList.sortedWith(compareByDescending {
                it.lastMessage?.manageCreatedAt()
            })

            setupChatListingAdapter()

            hideLoading()
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun setupChatListingAdapter() {
/*
        if (messagesListingAdapter != null)
            messagesListingAdapter?.addData(roomChatList)
        else {
            messagesListingAdapter =
                MessagesListingAdapter(roomChatList, messagesViewModel.getCurrentUser()?.userId!!, this)
           fragmentMessageBinding.rvChatListing.apply {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                adapter = messagesListingAdapter
            }
        }
*/

       fragmentMessageBinding.textViewNoMessages.isVisible = messagesListingAdapter?.itemCount == 0

    }
    override fun onClick(v: View?) {

    }

    override fun onItemClick(chatUserListing: ChatListData) {
/*
        val oppositeUserId: Int =
            if (chatUserListing.user1?.id?.toInt() == messagesViewModel.getCurrentUser()?.userId) {
                chatUserListing.user2?.id?.toInt()!!
            } else {
                chatUserListing.user1?.id?.toInt()!!
            }
*/

        findNavController().navigate(R.id.action_messages_to_chat)
    }
    override fun onResume() {
        super.onResume()
        observeViewModel()
        try {
            //   if (isActive) {
            showLoading("")
           messagesViewModel.getChats()
            //   }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val loggedInUserId =messagesViewModel.getCurrentUser()?.userId?.toLong()

       fragmentMessageBinding.editTextSearch.doAfterTextChanged { search ->
            if (!search.isNullOrEmpty()) {
                searchedChatList.clear()
                roomChatList.forEach {
                    if (it.user1?.id == loggedInUserId) {
                        if (it.user2?.name?.contains(search.toString(), true) == true) {
                            searchedChatList.add(it)
                        }
                    } else {
                        if (it.user1?.name?.contains(search.toString(), true) == true) {
                            searchedChatList.add(it)
                        }
                    }
                }
                if (searchedChatList.isNotEmpty()) {
                   fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
                   fragmentMessageBinding.rvChatListing.visibility = View.VISIBLE

//                    messagesListingAdapter?.addData(searchedChatList)
                } else {
                    // No Search Result found
                   fragmentMessageBinding.textViewNoMessages.visibility = View.VISIBLE
                   fragmentMessageBinding.rvChatListing.visibility = View.GONE
                }

            } else {
                // if search list is empty
               fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
               fragmentMessageBinding.rvChatListing.visibility = View.VISIBLE
//                messagesListingAdapter?.customNotify(roomChatList)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        isActive = false
        if (chatChannelRegistration != null)
            chatChannelRegistration?.remove()

    }

}