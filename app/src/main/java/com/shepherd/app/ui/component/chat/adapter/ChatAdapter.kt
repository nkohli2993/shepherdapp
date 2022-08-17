package com.shepherd.app.ui.component.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.databinding.AdapterChatBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.ChatViewModel


class ChatAdapter(
    private val viewModel: ChatViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
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
        //  return requestList.size
        return 10
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class ChatViewHolder(private val itemBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: DashboardModel, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    dashboard
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

    fun addData(dashboard: MutableList<String>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}