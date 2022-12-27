package com.shepherdapp.app.ui.component.joinCareTeam.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.invitation.Results
import com.shepherdapp.app.databinding.AdapterJoinCareTeamBinding
import com.shepherdapp.app.view_model.CareTeamsViewModel
import com.squareup.picasso.Picasso


class JoinCareTeamAdapter(
    private val viewModel: CareTeamsViewModel,
    var results: MutableList<Results> = ArrayList()
) :
    RecyclerView.Adapter<JoinCareTeamAdapter.ContentViewHolder>() {
    lateinit var binding: AdapterJoinCareTeamBinding

    private var onItemClickListener: OnItemClickListener? = null

    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    fun updateCareTeams(results: ArrayList<Results>) {
        this.results = results
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(id: Results?)
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
        return results.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(results[position])
    }

    inner class ContentViewHolder constructor(private var itemBinding: AdapterJoinCareTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Results?) {
            if (result == null) return
            itemBinding.data = result
            itemBinding.let {
                // Set Name  of loved One
                it.textViewCareTeamName.text = result.name

                // Set Profile Pic of loved One
                Picasso.get().load(result.image)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imageViewCareTeam)

                // Set Role of Care Team
                it.textViewCareTeamRole.text = "As " + result.careRoles?.name

                // Set toggle
                binding.toggleSwitch.isChecked = result.isSelected

            }

            binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                result.isSelected = isChecked

                if (isChecked) {
                    onItemClickListener?.onItemClick(result)
                }
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