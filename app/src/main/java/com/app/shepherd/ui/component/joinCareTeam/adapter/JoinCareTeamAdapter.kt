package com.app.shepherd.ui.component.joinCareTeam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.AdapterJoinCareTeamBinding
import com.app.shepherd.ui.component.joinCareTeam.JoinCareTeamViewModel
import com.app.shepherd.view_model.CareTeamsViewModel


class JoinCareTeamAdapter(
    private val viewModel: CareTeamsViewModel,
    var careTeams: MutableList<CareTeam> = ArrayList()
) :
    RecyclerView.Adapter<JoinCareTeamAdapter.ContentViewHolder>() {
    lateinit var binding: AdapterJoinCareTeamBinding

    private var onItemClickListener: OnItemClickListener? = null

    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    fun updateCareTeams(careTeams: ArrayList<CareTeam>) {
        this.careTeams = careTeams
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(careTeam: CareTeam)
    }

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
        return careTeams.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(careTeams[position])
    }

    inner class ContentViewHolder constructor(private var itemBinding: AdapterJoinCareTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(careTeam: CareTeam?) {
            if(careTeam == null) return
            itemBinding.data = careTeam
            binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                careTeam.isSelected = isChecked

                onItemClickListener?.onItemClick(careTeam)
            }
            itemBinding.cardView.setOnClickListener { itemBinding.toggleSwitch.performClick() }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}