package com.shepherd.app.ui.component.addLovedOneCondition.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.medical_conditions.Conditions
import com.shepherd.app.databinding.AdapterAddLovedOneConditionBinding
import com.shepherd.app.view_model.AddLovedOneConditionViewModel


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
        conditionList = conditions
        Log.d(TAG, "updateConditions:$conditionList ")
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
            binding.checkbox.isChecked = false
            if (conditions.isSelected) {
                binding.checkbox.isChecked = true
            }
            binding.checkbox.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (compoundButton.isPressed) {
                    onItemClickListener?.itemSelected(absoluteAdapterPosition)

                }
            }
            itemBinding.cardView.setOnClickListener { itemBinding.checkbox.performClick() }
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