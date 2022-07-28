package com.shepherd.app.ui.component.addMedicationDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.databinding.AdapterAddMedicationDoseBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.ui.component.addMedicationDetails.AddMedicationDetailViewModel


class AddMedicationDoseAdapter(
    private val viewModel: AddMedicationDetailViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<AddMedicationDoseAdapter.AddMedicationDoseViewHolder>() {
    lateinit var binding: AdapterAddMedicationDoseBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
           // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMedicationDoseViewHolder {
        context = parent.context
        binding =
            AdapterAddMedicationDoseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddMedicationDoseViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: AddMedicationDoseViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class AddMedicationDoseViewHolder(private val itemBinding: AdapterAddMedicationDoseBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: String, recyclerItemListener: RecyclerItemListener) {
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