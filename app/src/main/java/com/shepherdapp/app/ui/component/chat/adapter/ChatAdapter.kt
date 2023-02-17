package com.shepherdapp.app.ui.component.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.chat.MessageData
import com.shepherdapp.app.databinding.AdapterChatBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.changeDateFormat
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.ChatViewModel


class ChatAdapter(
    private val viewModel: ChatViewModel,
    var requestList: MutableList<MessageData> = ArrayList()
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    lateinit var binding: AdapterChatBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        context = parent.context
        binding =
            AdapterChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class ChatViewHolder(private val itemBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(messageData: MessageData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.messageData = messageData
            // Get loggedIn User Id
            val loggedInUserId = Prefs.with(ShepherdApp.appContext)!!.getInt(Const.USER_ID)
            itemBinding.userId = loggedInUserId.toString()
            // Check if loggedIn User is the sender of the message
            /* if (messageData.senderID == loggedInUserId.toString()) {
                 itemBinding.cvSender.visibility = View.VISIBLE
             } else {
                 itemBinding.cvReceiver.visibility = View.VISIBLE
             }*/

            itemBinding.executePendingBindings()

            itemBinding.tvReceivedTime.text = messageData.date?.changeDateFormat(
                sourceDateFormat = "yyyy-MM-dd HH:mm:ss",
                targetDateFormat = "hh:mm a"
            )
            itemBinding.tvSendTime.text = messageData.date?.changeDateFormat(
                sourceDateFormat = "yyyy-MM-dd HH:mm:ss",
                targetDateFormat = "hh:mm a"
            )

            itemBinding.imageViewUserSender.setImageFromUrl(
                "",
                messageData.senderName, ""
            )

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    messageData
                )
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(messageData: MutableList<MessageData>) {
        this.requestList.clear()
        this.requestList.addAll(messageData)
        notifyDataSetChanged()
    }

}