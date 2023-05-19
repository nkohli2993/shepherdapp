package com.shepherdapp.app.ui.component.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterAssigneeUsersBinding
import com.shepherdapp.app.utils.setImageFromUrl

class AdapterMemberCareTeam (
    var usersList: ArrayList<CareTeamModel>,
    val listener: AssigneeSelected,
) :
    RecyclerView.Adapter<AdapterMemberCareTeam.CarePointsAssigneeViewHolder>() {
    lateinit var binding: AdapterAssigneeUsersBinding
    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarePointsAssigneeViewHolder {
        context = parent.context
        binding =
            AdapterAssigneeUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsAssigneeViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return   usersList.size

    }

    override fun onBindViewHolder(holder: CarePointsAssigneeViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class CarePointsAssigneeViewHolder(private val itemBinding: AdapterAssigneeUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(position: Int) {
            val userDetail = usersList[position]
            itemBinding.imageViewCareTeam.setImageFromUrl(
                userDetail.user_id_details!!.profilePhoto,
                userDetail.user_id_details!!.firstname,
                userDetail.user_id_details!!.lastname
            )
            itemBinding.textViewCareTeamName.text = userDetail.user_id_details!!.firstname+ " "+userDetail.user_id_details!!.lastname
            itemBinding.root.setOnClickListener { listener.onAssigneeSelected(userDetail) }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface  AssigneeSelected{
        fun onAssigneeSelected(detail: CareTeamModel)
    }
    fun updateCareTeams(careTeams: ArrayList<CareTeamModel>) {
        this.usersList = careTeams
        notifyDataSetChanged()
    }

}