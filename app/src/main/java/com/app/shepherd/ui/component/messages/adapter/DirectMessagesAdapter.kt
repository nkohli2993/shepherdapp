package com.app.shepherd.ui.component.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterDirectMessagesBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.messages.MessagesViewModel


class DirectMessagesAdapter(
    private val viewModel: MessagesViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<DirectMessagesAdapter.DirectMessagesViewHolder>() {
    lateinit var binding: AdapterDirectMessagesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
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
        //  return requestList.size
        return 3
    }

    override fun onBindViewHolder(holder: DirectMessagesViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class DirectMessagesViewHolder(private val itemBinding: AdapterDirectMessagesBinding) :
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