package com.app.shepherd.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterMedicalHistoryTopicsBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.resources.ResourcesViewModel


class MedicalHistoryTopicsAdapter(
    private val viewModel: ResourcesViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<MedicalHistoryTopicsAdapter.MedicalHistoryTopicsViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalHistoryTopicsViewHolder {
        context = parent.context
        binding =
            AdapterMedicalHistoryTopicsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MedicalHistoryTopicsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 3
    }

    override fun onBindViewHolder(holder: MedicalHistoryTopicsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class MedicalHistoryTopicsViewHolder(private val itemBinding: AdapterMedicalHistoryTopicsBinding) :
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