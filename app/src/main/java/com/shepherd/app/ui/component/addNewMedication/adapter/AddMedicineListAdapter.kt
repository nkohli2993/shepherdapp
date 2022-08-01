package com.shepherd.app.ui.component.addNewMedication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.databinding.AdapterAddMedicineListBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.AddMedicationViewModel


class AddMedicineListAdapter(
    private val viewModel: AddMedicationViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<AddMedicineListAdapter.AddMedicineListViewHolder>() {
    lateinit var binding: AdapterAddMedicineListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
           // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMedicineListViewHolder {
        context = parent.context
        binding =
            AdapterAddMedicineListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddMedicineListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 10
    }

    override fun onBindViewHolder(holder: AddMedicineListViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class AddMedicineListViewHolder(private val itemBinding: AdapterAddMedicineListBinding) :
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