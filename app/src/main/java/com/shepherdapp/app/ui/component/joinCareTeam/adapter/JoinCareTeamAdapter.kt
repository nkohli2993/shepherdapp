package com.shepherdapp.app.ui.component.joinCareTeam.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.invitation.Results
import com.shepherdapp.app.databinding.AdapterJoinCareTeamBinding
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.CareTeamsViewModel


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
        fun onItemClick(id: Results?,position: Int)
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        //holder.bind(results[position],position)
        val result = results[position]
        holder.binding.let  {
            // Set Name  of loved One
            it.textViewCareTeamName.text = result.name

            it.toggleSwitch.isChecked = false
            if(result.isSelected){
                it.toggleSwitch.isChecked = true
            }
            // Set Profile Pic of loved One
            it.imageViewCareTeam.setImageFromUrl(
                result.image,
                result.name,
                ""
            )
            // Set Role of Care Team
            it.textViewCareTeamRole.text = "As " + result.careRoles?.name

        }

        /*binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            result.isSelected = isChecked
            if (isChecked && binding.toggleSwitch.isChecked) {
                binding.toggleSwitch.isChecked = true
                onItemClickListener?.onItemClick(result,position)
            }
            else{
                binding.toggleSwitch.isChecked = false
            }
        }*/
        binding.cardView.setOnClickListener {
            if (!result.isSelected) {
//                binding.toggleSwitch.isChecked = true
                onItemClickListener?.onItemClick(result,position)
            }
        }
    }


    class ContentViewHolder(itemView: AdapterJoinCareTeamBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: AdapterJoinCareTeamBinding = itemView

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}