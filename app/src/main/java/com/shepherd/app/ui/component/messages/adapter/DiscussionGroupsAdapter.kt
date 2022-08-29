package com.shepherd.app.ui.component.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.chat.ChatListData
import com.shepherd.app.data.dto.chat.ChatUserDetail
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.databinding.AdapterDiscussionGroupsBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.view_model.MessagesViewModel
import java.text.SimpleDateFormat


class DiscussionGroupsAdapter(
    private val viewModel: MessagesViewModel,
    var requestList: MutableList<ChatListData?> = ArrayList()
) :
    RecyclerView.Adapter<DiscussionGroupsAdapter.DiscussionGroupViewHolder>() {
    lateinit var binding: AdapterDiscussionGroupsBinding
    lateinit var context: Context


    val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openChat(itemData[0] as ChatListData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscussionGroupViewHolder {
        context = parent.context
        binding =
            AdapterDiscussionGroupsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DiscussionGroupViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: DiscussionGroupViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    inner class DiscussionGroupViewHolder(private val itemBinding: AdapterDiscussionGroupsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
            Const.USER_DETAILS,
            UserProfile::class.java
        )

        fun bind(chatListData: ChatListData?, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard

            val data = chatListData?.usersDataMap?.filter {
                it.value?.id != loggedInUser?.id.toString()
            }?.map {
                it.value
            }
            // Set Name
            itemBinding.txtGroupName.text = chatListData?.groupName

            // Set Message
            itemBinding.txtMessage.text = chatListData?.latestMessage

            // Set time
            val date = chatListData?.updated_at?.toDate()
            val sdf = SimpleDateFormat("hh:mm a")
            val formattedTime = sdf.format(date)
            itemBinding.txtTime.text = formattedTime

            // Set Unread count for loggedIn User

            val loggedInUserData = chatListData?.usersDataMap?.filter {
                it.value?.id == loggedInUser?.id.toString()
            }?.map {
                it.value
            }
            if (loggedInUserData?.get(0)?.unreadCount == 0) {
                itemBinding.txtUnreadCount.visibility = View.GONE
            } else {
                itemBinding.txtUnreadCount.visibility = View.VISIBLE
            }

            // Set Unread count
//            itemBinding.txtUnreadCount.text = data?.get(0)?.unreadCount.toString()

            /*  // profile pics of members
              var membersPic = data?.map {
                  it?.imageUrl
              } as ArrayList*/

            val adapter = DiscussionGroupMemberAdapter(viewModel)
            itemBinding.rvMembersDiscussionsGroup.adapter = adapter
            adapter.addData(data as ArrayList<ChatUserDetail?>)


            itemBinding.root.setOnClickListener {
                chatListData.let { it1 ->
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(chatListData: ArrayList<ChatListData?>) {
        this.requestList.clear()
        this.requestList.addAll(chatListData)
        notifyDataSetChanged()
    }

}