package com.shepherdapp.app.ui.component.eventDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.dashboard.DashboardModel
import com.shepherdapp.app.databinding.AdapterEventChatBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.ui.component.eventDetails.EventDetailsViewModel


class EventChatAdapter(
    private val viewModel: EventDetailsViewModel,
    var requestList: MutableList<DashboardModel> = ArrayList()
) :
    RecyclerView.Adapter<EventChatAdapter.EventChatViewHolder>() {
    lateinit var binding: AdapterEventChatBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
           // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventChatViewHolder {
        context = parent.context
        binding =
            AdapterEventChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return EventChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 6
    }

    override fun onBindViewHolder(holder: EventChatViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class EventChatViewHolder(private val itemBinding: AdapterEventChatBinding) :
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

    fun addData(dashboard: MutableList<DashboardModel>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}