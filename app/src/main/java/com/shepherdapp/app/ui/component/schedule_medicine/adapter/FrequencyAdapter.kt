package com.shepherdapp.app.ui.component.schedule_medicine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.FrequencyData
import com.shepherdapp.app.databinding.AdapterFrequencyBinding

class FrequencyAdapter(
    val context: Context,
    val onListener: selectedFrequency,
    var frequency: ArrayList<FrequencyData> = ArrayList()
) :
    RecyclerView.Adapter<FrequencyAdapter.FrequencyListViewHolder>() {
    lateinit var binding: AdapterFrequencyBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrequencyListViewHolder {
        binding =
            AdapterFrequencyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FrequencyListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return frequency.size
    }

    override fun onBindViewHolder(holder: FrequencyListViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class FrequencyListViewHolder(private val itemBinding: AdapterFrequencyBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            itemBinding.textViewTitle.text = frequency[position].name
            itemBinding.textViewTitle.setOnClickListener {
                onListener.onSelected(position)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface selectedFrequency {
        fun onSelected(position: Int)
    }

    fun setData(frequency : ArrayList<FrequencyData>){
        this.frequency = frequency
        notifyDataSetChanged()
    }
}