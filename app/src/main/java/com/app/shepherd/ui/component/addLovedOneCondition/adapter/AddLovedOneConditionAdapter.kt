package com.app.shepherd.ui.component.addLovedOneCondition.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.medical_conditions.Conditions
import com.app.shepherd.databinding.AdapterAddLovedOneConditionBinding
import com.app.shepherd.view_model.AddLovedOneConditionViewModel


class AddLovedOneConditionAdapter(
    private val viewModel: AddLovedOneConditionViewModel,
    var conditionList: MutableList<Conditions> = ArrayList()
) :
    RecyclerView.Adapter<AddLovedOneConditionAdapter.ContentViewHolder>() {
    lateinit var binding: AdapterAddLovedOneConditionBinding

    private var onItemClickListener: ItemSelectedListener? = null

    fun setClickListener(clickListener: ItemSelectedListener) {
        onItemClickListener = clickListener
    }

    fun updateConditions(conditions: ArrayList<Conditions>) {
        conditionList = conditions
        notifyDataSetChanged()
    }

    interface ItemSelectedListener {
        fun itemSelected(conditions: Conditions)
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
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                conditions.isSelected = isChecked

                onItemClickListener?.itemSelected(conditions)
            }
            itemBinding.cardView.setOnClickListener { itemBinding.checkbox.performClick() }
            itemBinding.textViewCondition.setOnClickListener { itemBinding.checkbox.performClick() }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}