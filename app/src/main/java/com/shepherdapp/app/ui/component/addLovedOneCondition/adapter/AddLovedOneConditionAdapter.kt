package com.shepherdapp.app.ui.component.addLovedOneCondition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.medical_conditions.Conditions
import com.shepherdapp.app.databinding.AdapterAddLovedOneConditionBinding
import com.shepherdapp.app.view_model.AddLovedOneConditionViewModel


class AddLovedOneConditionAdapter(
    private val viewModel: AddLovedOneConditionViewModel,
    var conditionList: MutableList<Conditions> = ArrayList()
) :
    RecyclerView.Adapter<AddLovedOneConditionAdapter.ContentViewHolder>() {
    private val TAG = "AddLovedOneConAdapter"
    lateinit var binding: AdapterAddLovedOneConditionBinding

    private var onItemClickListener: ItemSelectedListener? = null

    fun setClickListener(clickListener: ItemSelectedListener) {
        onItemClickListener = clickListener
    }

    fun updateConditions(conditions: ArrayList<Conditions>) {
        this.conditionList = conditions
        notifyDataSetChanged()
    }
    fun addConditions(conditions: ArrayList<Conditions>) {
        this.conditionList.addAll(conditions)
        notifyDataSetChanged()
    }

    interface ItemSelectedListener {
        fun itemSelected(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_add_loved_one_condition,
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return conditionList.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(conditionList[position])
    }

    inner class ContentViewHolder(private var itemBinding: AdapterAddLovedOneConditionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(conditions: Conditions) {
            itemBinding.data = conditions
            itemBinding.checkbox.isChecked = false
            if (conditions.isSelected) {
                itemBinding.checkbox.isChecked = true
            }

            itemBinding.cardView.setOnClickListener {  onItemClickListener?.itemSelected(absoluteAdapterPosition) }
//            itemBinding.textViewCondition.setOnClickListener { itemBinding.checkbox.performClick() }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}