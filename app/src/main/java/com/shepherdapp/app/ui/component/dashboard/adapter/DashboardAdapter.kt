package com.shepherdapp.app.ui.component.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.dashboard.DashboardModel
import com.shepherdapp.app.databinding.AdapterDashboardBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.DashboardViewModel


class DashboardAdapter(
    private val viewModel: DashboardViewModel,
    var requestList: MutableList<DashboardModel> = ArrayList()
) :
    RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {
    lateinit var binding: AdapterDashboardBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        context = parent.context
        binding =
            AdapterDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DashboardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class DashboardViewHolder(private val itemBinding: AdapterDashboardBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: DashboardModel, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = dashboard
            itemBinding.buttonView.setOnClickListener {
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