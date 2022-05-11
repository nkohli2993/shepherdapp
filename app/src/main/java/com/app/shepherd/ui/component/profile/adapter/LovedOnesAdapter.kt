package com.app.shepherd.ui.component.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterLovedOnesBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.notifications.NotificationsViewModel
import com.app.shepherd.ui.component.profile.ProfileViewModel


class LovedOnesAdapter(
    private val viewModel: ProfileViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<LovedOnesAdapter.LovedOnesViewHolder>() {
    lateinit var binding: AdapterLovedOnesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LovedOnesViewHolder {
        context = parent.context
        binding =
            AdapterLovedOnesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LovedOnesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 2
    }

    override fun onBindViewHolder(holder: LovedOnesViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class LovedOnesViewHolder(private val itemBinding: AdapterLovedOnesBinding) :
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