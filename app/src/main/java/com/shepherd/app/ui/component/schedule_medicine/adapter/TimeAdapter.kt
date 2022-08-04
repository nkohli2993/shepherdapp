package com.shepherd.app.ui.component.schedule_medicine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.schedule_medlist.TimeSelectedlist
import com.shepherd.app.databinding.LayoutAddTimeBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.AddMedicationViewModel


@SuppressLint("NotifyDataSetChanged")
class TimeAdapter(
    private val viewModel: AddMedicationViewModel,
    val context: Context,
    var timeList: MutableList<TimeSelectedlist> = ArrayList()
) :
    RecyclerView.Adapter<TimeAdapter.AddTimeViewHolder>() {
    lateinit var binding: LayoutAddTimeBinding


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.setSelectedTime(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddTimeViewHolder {
        binding =
            LayoutAddTimeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddTimeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return timeList.size
    }

    override fun onBindViewHolder(holder: AddTimeViewHolder, position: Int) {
        holder.bind(timeList[position], onItemClickListener)
    }


    inner class AddTimeViewHolder(private val itemBinding: LayoutAddTimeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(timelist: TimeSelectedlist, recyclerItemListener: RecyclerItemListener) {
            if ((timelist.time ?: "").isNotEmpty()) {
                itemBinding.selectedTimeTV.text = timelist.time
            }
            itemBinding.selectedTimeTV.doOnTextChanged { text, start, before, count ->
                if (itemBinding.selectedTimeTV.text.toString().isNotEmpty()) {
                    if (timelist.isAmPM == "am") {
                        setColorTimePicked(R.color._192032, R.color.colorBlackTrans50, itemBinding)
                    } else {
                        setColorTimePicked(R.color.colorBlackTrans50, R.color._192032, itemBinding)
                    }
                }
            }
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    absoluteAdapterPosition
                )
            }
        }

        private fun setColorTimePicked(
            selected: Int,
            unselected: Int,
            binding: LayoutAddTimeBinding
        ) {
            binding.tvam.setTextColor(
                ContextCompat.getColor(
                    context.applicationContext,
                    selected
                )
            )
            binding.tvpm.setTextColor(
                ContextCompat.getColor(
                    context.applicationContext,
                    unselected
                )
            )
        }

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(timeListAdded: MutableList<TimeSelectedlist>) {
        timeList.addAll(timeListAdded)
        notifyDataSetChanged()
    }

    fun removeData(timeListAdded: MutableList<TimeSelectedlist>) {
        timeList.clear()
        timeList.addAll(timeListAdded)
        notifyDataSetChanged()
    }

}