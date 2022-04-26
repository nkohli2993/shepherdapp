package com.app.shepherd.ui.component.addLovedOneCondition.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.databinding.AdapterAddLovedOneConditionBinding
import com.app.shepherd.ui.component.addLovedOneCondition.AddLovedOneConditionViewModel


class AddLovedOneConditionAdapter(
    private val viewModel: AddLovedOneConditionViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding: AdapterAddLovedOneConditionBinding

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
        return 10
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> holder.bind(requestList[position])
        }
    }

    inner class ContentViewHolder constructor(private var binding: AdapterAddLovedOneConditionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String?) {
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}