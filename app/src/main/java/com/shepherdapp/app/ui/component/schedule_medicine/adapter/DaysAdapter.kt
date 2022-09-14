package com.shepherdapp.app.ui.component.schedule_medicine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.DayList
import com.shepherdapp.app.databinding.LayoutAddMedicineListBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.AddMedicationViewModel


class DaysAdapter(
    private val viewModel: AddMedicationViewModel,
    val context: Context, var doseList: MutableList<DayList> = ArrayList()
) :
    RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {
    lateinit var binding: LayoutAddMedicineListBinding


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.setDayData(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayViewHolder {
        binding =
            LayoutAddMedicineListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DayViewHolder(binding)
    }

    override fun getItemCount(): Int {

        return doseList.size
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(doseList[position], onItemClickListener)
    }


    inner class DayViewHolder(private val itemBinding: LayoutAddMedicineListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(timelist: DayList, recyclerItemListener: RecyclerItemListener) {
            if ((timelist.time ?: "").isNotEmpty()) {
                itemBinding.cbDay.text = timelist.time
            }
            itemBinding.cbDay.setButtonDrawable(R.drawable.checkbox_selectot_grey)
            itemBinding.cbDay.setTextColor(ContextCompat.getColor(context, R.color._A3A5AD))
            if (timelist.isClickabled) {
                itemBinding.cbDay.setButtonDrawable(R.drawable.checkbox_selector)
                itemBinding.cbDay.setTextColor(ContextCompat.getColor(context, R.color._192032))
            }
            itemBinding.cbDay.isChecked = false
            if (timelist.isSelected) {
                itemBinding.cbDay.isChecked = true
            }
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

    @SuppressLint("NotifyDataSetChanged")
    fun addData(doseListAdded: MutableList<DayList>) {
        doseList.addAll(doseListAdded)
        notifyDataSetChanged()
    }
}