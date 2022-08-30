package com.shepherd.app.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.databinding.AdapterMedicalHistoryTopicsBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.ui.component.resources.ResourcesViewModel


class MedicalHistoryTopicsAdapter(
    private val viewModel: ResourcesViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<MedicalHistoryTopicsAdapter.MedicalHistoryTopicsViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openResourceItems(itemData[0] as Int)
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
        holder.bind("", onItemClickListener)
    }


    class MedicalHistoryTopicsViewHolder(private val itemBinding: AdapterMedicalHistoryTopicsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: String, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    absoluteAdapterPosition
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