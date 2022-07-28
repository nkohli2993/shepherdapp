package com.shepherd.app.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.databinding.AdapterMedicalHistoryBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.ui.component.resources.ResourcesViewModel


class MedicalHistoryAdapter(
    private val viewModel: ResourcesViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<MedicalHistoryAdapter.MedicalHistoryViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalHistoryViewHolder {
        context = parent.context
        binding =
            AdapterMedicalHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MedicalHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 3
    }

    override fun onBindViewHolder(holder: MedicalHistoryViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class MedicalHistoryViewHolder(private val itemBinding: AdapterMedicalHistoryBinding) :
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