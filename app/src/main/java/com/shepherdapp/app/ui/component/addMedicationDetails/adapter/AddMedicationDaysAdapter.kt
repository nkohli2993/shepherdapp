package com.shepherdapp.app.ui.component.addMedicationDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.databinding.AdapterAddMedicationDaysBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.ui.component.addMedicationDetails.AddMedicationDetailViewModel


class AddMedicationDaysAdapter(
    private val viewModel: AddMedicationDetailViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<AddMedicationDaysAdapter.AddMedicationDaysViewHolder>() {
    lateinit var binding: AdapterAddMedicationDaysBinding
    lateinit var context: Context

    init {
        requestList.add("Monday")
        requestList.add("Tuesday")
        requestList.add("Wednesday")
        requestList.add("Thursday")
        requestList.add("Friday")
        requestList.add("Saturday")
    }

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMedicationDaysViewHolder {
        context = parent.context
        binding =
            AdapterAddMedicationDaysBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddMedicationDaysViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: AddMedicationDaysViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class AddMedicationDaysViewHolder(private val itemBinding: AdapterAddMedicationDaysBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: String, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = dashboard
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