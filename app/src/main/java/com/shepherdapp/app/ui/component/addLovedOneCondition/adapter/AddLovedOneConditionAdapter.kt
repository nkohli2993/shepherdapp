package com.shepherdapp.app.ui.component.addLovedOneCondition.adapter

import android.view.LayoutInflater
import android.view.View
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
    private var itemEditClickListener: ItemEditClickListener? = null

    fun setClickListener(
        clickListener: ItemSelectedListener,
        itemEditListener: ItemEditClickListener
    ) {
        onItemClickListener = clickListener
        itemEditClickListener = itemEditListener
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

    interface ItemEditClickListener {
        fun itemEditSelected(conditions: Conditions)
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
            // Check if the medical condition is being created by the user
            if (conditions.createdBy == "user") {
                itemBinding.imgEditMedicalCondition.visibility = View.VISIBLE
            } else {
                itemBinding.imgEditMedicalCondition.visibility = View.GONE
            }

            itemBinding.cardView.setOnClickListener {
                onItemClickListener?.itemSelected(
                    absoluteAdapterPosition
                )
            }

            // Handle click of edit medical condition
            itemBinding.imgEditMedicalCondition.setOnClickListener {
                itemEditClickListener?.itemEditSelected(conditions)
            }

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