package com.app.shepherd.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.databinding.AdapterSelectedDayMedicineBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.MedListViewModel


class SelectedDayMedicineAdapter(
    private val viewModel: MedListViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<SelectedDayMedicineAdapter.SelectedDayMedicineViewHolder>() {
    lateinit var binding: AdapterSelectedDayMedicineBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
             viewModel.openMedDetail(itemData[0] as String)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedDayMedicineViewHolder {
        context = parent.context
        binding =
            AdapterSelectedDayMedicineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SelectedDayMedicineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: SelectedDayMedicineViewHolder, position: Int) {
//        holder.bind(requestList[position], onItemClickListener)
        holder.bind("", onItemClickListener)
    }


    class SelectedDayMedicineViewHolder(private val itemBinding: AdapterSelectedDayMedicineBinding) :
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