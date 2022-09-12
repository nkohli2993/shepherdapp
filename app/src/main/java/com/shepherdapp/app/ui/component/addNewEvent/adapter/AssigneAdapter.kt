package com.shepherdapp.app.ui.component.addNewEvent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterAssignToEventBinding
import com.squareup.picasso.Picasso

class AssigneAdapter(
    val onListener: AssignToEventAdapter.selectedTeamMember,
    val context: Context,
    var memberList: ArrayList<CareTeamModel> = ArrayList()
) :
    RecyclerView.Adapter<AssigneAdapter.AddAssigneListViewHolder>() {
    lateinit var binding: AdapterAssignToEventBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAssigneListViewHolder {
        binding =
            AdapterAssignToEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddAssigneListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    override fun onBindViewHolder(holder: AddAssigneListViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class AddAssigneListViewHolder(private val itemBinding: AdapterAssignToEventBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {

            itemBinding.textViewCareTeamName.text =
                memberList[position].user_id_details.firstname.plus(" ")
                    .plus(if (memberList[position].user_id_details.lastname == null) "" else memberList[position].user_id_details.lastname)
            itemBinding.textViewCareTeamRole.text = memberList[position].careRoles.name

            if (!memberList[position].user_id_details.profilePhoto.isNullOrEmpty()) {
                Picasso.get().load(memberList[position].user_id_details.profilePhoto)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(itemBinding.imageViewCareTeam)
            }
            itemBinding.checkbox.isChecked = false
            if (memberList[position].isSelected) {
                itemBinding.checkbox.isChecked = true
            }
            itemBinding.clEventWrapper.setOnClickListener {
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

    interface selectedTeamMember {
        fun onSelected(position: Int)
    }

    fun setData(careTeam: ArrayList<CareTeamModel>) {
        this.memberList = careTeam
        notifyDataSetChanged()
    }
}