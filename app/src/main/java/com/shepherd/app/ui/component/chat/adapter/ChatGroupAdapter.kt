package com.shepherd.app.ui.component.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.chat.MessageGroupData
import com.shepherd.app.databinding.AdapterChatGroupBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.extensions.getChatDate
import com.shepherd.app.view_model.ChatViewModel

/**
 * Created by Deepak Rattan on 19/08/22
 */
class ChatGroupAdapter(
    private val viewModel: ChatViewModel,
    var requestList: MutableList<MessageGroupData> = ArrayList(),
) : RecyclerView.Adapter<ChatGroupAdapter.ChatViewHolder>() {
    lateinit var binding: AdapterChatGroupBinding
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
            AdapterChatGroupBinding.inflate(
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


    inner class ChatViewHolder(private val itemBinding: AdapterChatGroupBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(chatData: MessageGroupData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = chatData
            binding.dateTV.text = chatData.date.getChatDate("yyyy-MM-dd")
            val adapter = ChatAdapter(viewModel)
            itemBinding.messageRV.adapter = adapter
            adapter.addData(chatData.messageList)


            /* itemBinding.root.setOnClickListener {
                 recyclerItemListener.onItemSelected(
                     chatData
                 )
             }*/
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(chatData: MutableList<MessageGroupData>) {
        this.requestList.clear()
        this.requestList.addAll(chatData)
        notifyDataSetChanged()
    }

}