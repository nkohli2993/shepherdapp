package com.shepherdapp.app.ui.component.careTeamMembers.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterCareTeamMembersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.CareTeamMembersViewModel
import com.squareup.picasso.Picasso


class CareTeamMembersAdapter(
    private val viewModel: CareTeamMembersViewModel,
    var careTeams: MutableList<CareTeamModel> = ArrayList()

) :
    RecyclerView.Adapter<CareTeamMembersAdapter.CareTeamViewHolder>() {
    lateinit var binding: AdapterCareTeamMembersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openMemberDetails(itemData[0] as CareTeamModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareTeamViewHolder {
        context = parent.context
        binding =
            AdapterCareTeamMembersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CareTeamViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return careTeams.size
    }

    override fun onBindViewHolder(holder: CareTeamViewHolder, position: Int) {
        holder.bind(position, onItemClickListener)
    }


    inner class CareTeamViewHolder(private val itemBinding: AdapterCareTeamMembersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            val careTeam = careTeams[position]
//            itemBinding.data = careTeam
            val firstName = careTeam.user_id_details.firstname
            val lastName = careTeam.user_id_details.lastname
            val fullName = "$firstName $lastName"
            val imageUrl = careTeam.user_id_details.profilePhoto

            itemBinding.let {
                it.textViewCareTeamName.text = fullName

                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imageViewCareTeam)

                it.textViewCareTeamRole.text = careTeam.careRoles.name
            }

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    careTeams[position]
                )
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    /*fun addData(dashboard: MutableList<String>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }*/

    fun updateCareTeams(careTeams: ArrayList<CareTeamModel>) {
        this.careTeams = careTeams
        notifyDataSetChanged()
    }

}