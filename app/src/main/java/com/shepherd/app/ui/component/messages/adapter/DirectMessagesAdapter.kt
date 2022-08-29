package com.shepherd.app.ui.component.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.chat.ChatListData
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.databinding.AdapterDirectMessagesBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.view_model.MessagesViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat


class DirectMessagesAdapter(
    private val viewModel: MessagesViewModel,
    var requestList: MutableList<ChatListData?> = ArrayList()
) : RecyclerView.Adapter<DirectMessagesAdapter.DirectMessagesViewHolder>() {
    lateinit var binding: AdapterDirectMessagesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openChat(itemData[0] as ChatListData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DirectMessagesViewHolder {
        context = parent.context
        binding =
            AdapterDirectMessagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DirectMessagesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: DirectMessagesViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class DirectMessagesViewHolder(private val itemBinding: AdapterDirectMessagesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
            Const.USER_DETAILS,
            UserProfile::class.java
        )

        fun bind(chatListData: ChatListData?, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = chatListData

            val data = chatListData?.usersDataMap?.filter {
                it.value?.id != loggedInUser?.id.toString()
            }?.map {
                it.value
            }

            if (!data?.get(0)?.imageUrl.isNullOrEmpty()) {
                //Load Image
                Picasso.get().load(data?.get(0)?.imageUrl ?: "")
                    .placeholder(R.drawable.ic_defalut_profile_pic).into(itemBinding.imgChatUser)
            }

            // Set Name
            itemBinding.txtName.text = data?.get(0)?.name

            // Set Message
            itemBinding.txtMessage.text = chatListData?.latestMessage

            /*itemBinding.tvSendTime.text = messageData.date?.changeDateFormat(
                sourceDateFormat = "yyyy-MM-dd HH:mm:ss",
                targetDateFormat = "hh:mm a"
            )*/
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

            itemBinding.txtUnreadCount.text = loggedInUserData?.get(0)?.unreadCount.toString()

            itemBinding.root.setOnClickListener {
                chatListData?.let { it1 ->
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