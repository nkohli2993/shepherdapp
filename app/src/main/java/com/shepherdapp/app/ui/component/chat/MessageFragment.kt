package com.shepherdapp.app.ui.component.chat

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.firebase.firestore.ListenerRegistration
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.UserAssigneDetail
import com.shepherdapp.app.data.dto.chat.ChatUserListing
import com.shepherdapp.app.databinding.FragmentMessageBinding
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.chat.adapter.MessagesListingAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.view_model.MessagesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(), View.OnClickListener,
    MessagesListingAdapter.OnItemClickListener {
    private var loggedInUserId: Long? = null
    private lateinit var fragmentMessageBinding: FragmentMessageBinding
    private var roomChatList: List<ChatUserListing> = ArrayList()
    private var searchedChatList: ArrayList<ChatUserListing> = ArrayList()
    private var messagesListingAdapter: MessagesListingAdapter? = null
    private var chatChannelRegistration: ListenerRegistration? = null
    private var isActive = false
    override fun getLayoutRes() = R.layout.fragment_message
    private val messagesViewModel: MessagesViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMessageBinding =
            FragmentMessageBinding.inflate(inflater, container, false)
        messagesViewModel.tableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.CARE_TEAM_CHATS
            } else {
                TableName.CARE_TEAM_CHATS_DEV
            }
        loggedInUserId = messagesViewModel.getCurrentUser()?.userId?.toLong()
        return fragmentMessageBinding.root
    }

    override fun initViewBinding() {
        fragmentMessageBinding.listener = this

    }

    override fun observeViewModel() {
        showLoading("")
        messagesViewModel.getChatRooms(messagesViewModel.getCurrentUser()?.userId!!) {
            roomChatList = it
            hideLoading()
            updateRecyclerView()
        }
    }

    private fun updateRecyclerView() {
        try {

            if (roomChatList.isNotEmpty())
                fragmentMessageBinding.textViewNoMessages.visibility = View.GONE

            roomChatList = roomChatList.sortedWith(compareByDescending {
                it.createdAt
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
        if (messagesListingAdapter != null)
            messagesListingAdapter?.addData(roomChatList)
        else {
            messagesListingAdapter =
                MessagesListingAdapter(
                    roomChatList,
                    messagesViewModel.getCurrentUser()?.userId!!,
                    this
                )
            fragmentMessageBinding.rvChatListing.apply {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                adapter = messagesListingAdapter
            }
        }

        fragmentMessageBinding.textViewNoMessages.isVisible = messagesListingAdapter?.itemCount == 0

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgCancel -> {
                fragmentMessageBinding.editTextSearch.setText("")
                fragmentMessageBinding.imgCancel.isVisible = false
                fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
                fragmentMessageBinding.rvChatListing.visibility = View.VISIBLE
                messagesListingAdapter?.addData(roomChatList)
            }
        }
    }

    override fun onItemClick(chatUserListing: ChatUserListing) {
        val detail =
            if (chatUserListing.user1?.userId?.toInt() == messagesViewModel.getCurrentUser()?.userId!!) {
                UserAssigneDetail(
                    chatUserListing.user2!!.userId!!.toInt(),
                    chatUserListing.user2.userId!!.toInt(),
                    chatUserListing.user2.firstname,
                    chatUserListing.user2.lastname, "", "", "", "",
                    chatUserListing.user2.profilePhoto,
                )
            } else {
                UserAssigneDetail(
                    chatUserListing.user1!!.userId!!.toInt(),
                    chatUserListing.user1.userId!!.toInt(),
                    chatUserListing.user1.firstname,
                    chatUserListing.user1.lastname,
                    "",
                    "",
                    "",
                    "",
                    chatUserListing.user1.profilePhoto,
                )

            }

        findNavController().navigate(
            R.id.action_new_message_to_chat,
            bundleOf("assignee_user" to detail)
        )
    }

    override fun onResume() {
        super.onResume()
        messagesListingAdapter = null
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


        fragmentMessageBinding.editTextSearch.doAfterTextChanged { search ->
            searchUserList(loggedInUserId, search)
        }
    }

    private fun searchUserList(loggedInUserId: Long?, search: Editable?) {
        if (!fragmentMessageBinding.editTextSearch.text.isNullOrEmpty()) {
            fragmentMessageBinding.imgCancel.isVisible = true
            searchedChatList.clear()
            roomChatList.forEach {
                if (it.user1?.userId == loggedInUserId) {
                    if (it.user2?.firstname.plus(" ${it.user2?.lastname}")
                            .contains(search.toString(), true)
                    ) {
                        searchedChatList.add(it)
                    }
                } else {
                    if (it.user1?.firstname.plus(" ${it.user1?.lastname}")
                            .contains(search.toString(), true)
                    ) {
                        searchedChatList.add(it)
                    }
                }
            }
            if (searchedChatList.isNotEmpty()) {
                fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
                fragmentMessageBinding.rvChatListing.visibility = View.VISIBLE

                messagesListingAdapter?.addData(searchedChatList)
            } else {
                // No Search Result found
                fragmentMessageBinding.textViewNoMessages.visibility = View.VISIBLE
                fragmentMessageBinding.rvChatListing.visibility = View.GONE
            }

        } else {
            // if search list is empty
            fragmentMessageBinding.imgCancel.isVisible = false
            fragmentMessageBinding.textViewNoMessages.visibility = View.GONE
            fragmentMessageBinding.rvChatListing.visibility = View.VISIBLE
            messagesListingAdapter?.addData(roomChatList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isActive = false
        if (chatChannelRegistration != null)
            chatChannelRegistration?.remove()

    }

}