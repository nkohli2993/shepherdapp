package com.app.shepherd.ui.component.addLovedOneCondition.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.databinding.AdapterAddLovedOneConditionBinding
import com.app.shepherd.databinding.AdapterJoinCareTeamBinding
import com.app.shepherd.ui.component.addLovedOneCondition.AddLovedOneConditionViewModel
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamViewModel


class JoinCareTeamAdapter(
    private val viewModel: JoinCareTeamViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding: AdapterJoinCareTeamBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_join_care_team,
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            //is ContentViewHolder -> holder.bind(requestList[position])
        }
    }

    inner class ContentViewHolder constructor(private var binding: AdapterJoinCareTeamBinding) :
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