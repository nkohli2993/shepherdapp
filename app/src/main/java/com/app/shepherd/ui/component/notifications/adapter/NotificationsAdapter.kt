package com.app.shepherd.ui.component.notifications.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterNotificationsBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.myMedList.MyMedListViewModel
import com.app.shepherd.ui.component.notifications.NotificationsViewModel


class NotificationsAdapter(
    private val viewModel: NotificationsViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>() {
    lateinit var binding: AdapterNotificationsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsViewHolder {
        context = parent.context
        binding =
            AdapterNotificationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return NotificationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 5
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class NotificationsViewHolder(private val itemBinding: AdapterNotificationsBinding) :
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