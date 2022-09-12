package com.shepherdapp.app.ui.component.schedule_medicine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.DoseList
import com.shepherdapp.app.databinding.AdapterFrequencyBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.AddMedicationViewModel

class DoseAdapter (
    private val viewModel: AddMedicationViewModel,
    val context: Context,
    var doseList: MutableList<DoseList> = ArrayList()
) :
    RecyclerView.Adapter<DoseAdapter.AddTimeViewHolder>() {
    lateinit var binding: AdapterFrequencyBinding


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.setSelectedDose(itemData[0] as DoseList)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddTimeViewHolder {
        binding =
            AdapterFrequencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddTimeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return doseList.size
    }

    override fun onBindViewHolder(holder: AddTimeViewHolder, position: Int) {
        holder.bind(doseList[position], onItemClickListener)
    }


    inner class AddTimeViewHolder(private val itemBinding: AdapterFrequencyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(timelist: DoseList, recyclerItemListener: RecyclerItemListener) {
            if ((timelist.name ?: "").isNotEmpty()) {
                itemBinding.textViewTitle.text = timelist.name
            }
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    timelist
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

    @SuppressLint("NotifyDataSetChanged")
    fun addData(doseListAdded: MutableList<DoseList>) {
        doseList.addAll(doseListAdded)
        notifyDataSetChanged()
    }


}