package com.shepherdapp.app.ui.component.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.databinding.AdapterCareChatDayBinding
import com.shepherdapp.app.utils.extensions.getChatDate
import com.shepherdapp.app.view_model.ChatViewModel
import java.util.ArrayList


class ChatAdapter(
    private val viewModel: ChatViewModel,
    var messageList: ArrayList<MessageGroupData> = ArrayList(),
) : RecyclerView.Adapter<ChatAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterCareChatDayBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterCareChatDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return messageList.size

    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterCareChatDayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(position: Int) {
            val chatData = messageList[position]

            binding.dateTV.text = chatData.date.getChatDate("yyyy-MM-dd")
            val adapter = CareTeamChatAdapter()
            itemBinding.messageRV.adapter = adapter
            adapter.addData(chatData.messageList)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(chatData: MutableList<MessageGroupData>) {
        this.messageList.clear()
        this.messageList.addAll(chatData)
        notifyDataSetChanged()
    }


}