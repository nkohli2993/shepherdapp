package com.shepherdapp.app.ui.component.notifications.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.notification.Data
import com.shepherdapp.app.databinding.AdapterNotificationsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.extensions.convertTimeStampToDate
import com.shepherdapp.app.utils.extensions.convertTimeStampToTime
import com.shepherdapp.app.view_model.NotificationsViewModel


class NotificationsAdapter(
    private val viewModel: NotificationsViewModel,
    var requestList: MutableList<Data> = ArrayList()
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
        return requestList.size
    }

    override fun onBindViewHolder(holder: NotificationsViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class NotificationsViewHolder(private val itemBinding: AdapterNotificationsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(data: Data, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = data
            val date = data.notification?.createdAt
            val formattedDate = date.convertTimeStampToDate()
            val time = date.convertTimeStampToTime()
            itemBinding.textViewDescription.text = "$formattedDate at $time"

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    data
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

    fun addData(dashboard: MutableList<Data>) {
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}