package com.shepherdapp.app.ui.component.careTeamMembers.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterCareTeamMembersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.*
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
            // First parameter is the CareTeamModel and 2nd is the click type
            viewModel.openMemberDetails(itemData[0] as CareTeamModel, itemData[1] as Int)
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
            val firstName = careTeam.user_id_details?.firstname
            val lastName = careTeam.user_id_details?.lastname
            val first = firstName?.first().toString()
            var last: String? = null
            var fullName: String? = null
            if (lastName != null) {
                last = lastName.first().toString()
                fullName = "$first$last"
            } else {
                fullName = first
            }

            val imageUrl = careTeam.user_id_details?.profilePhoto

            if (careTeam.isPendingInvite == true) {
                itemBinding.cardView.visibility = View.VISIBLE
                itemBinding.imageViewInfo.visibility = View.VISIBLE
                itemBinding.imageViewDelete.isClickable = true

                itemBinding.llImageWrapper.alpha = 0.4f
                itemBinding.textViewCareTeamName.alpha = 0.4f
                itemBinding.textViewCareTeamRole.alpha = 0.4f
                itemBinding.imageViewDelete.setBackgroundResource(R.drawable.ic_delete_pending_invite)
                itemBinding.imageViewInfo.setBackgroundResource(R.drawable.ic_waiting)

                // Set top margin
                itemBinding.imageViewDelete.margin(top = 30f)
                itemBinding.imageViewInfo.margin(top = 30f)

                itemBinding.let {
                    it.textViewCareTeamName.text = careTeam.email
                    it.imageViewCareTeam.setImageFromUrl(
                        careTeam.image,
                        careTeam.email, ""
                    )

                    it.textViewCareTeamRole.text = careTeam.careRoles?.name
                }

                // Handle click of delete button
                itemBinding.imageViewDelete.setOnClickListener {
                    recyclerItemListener.onItemSelected(
                        careTeams[position],
                        ClickType.Delete.value
                    )
                }
            } else {
                itemBinding.cardView.visibility = View.VISIBLE
                itemBinding.imageViewInfo.visibility = View.GONE
                itemBinding.imageViewDelete.isClickable = false
                itemBinding.llImageWrapper.alpha = 1f
                itemBinding.textViewCareTeamName.alpha = 1f
                itemBinding.textViewCareTeamRole.alpha = 1f
                itemBinding.imageViewDelete.setBackgroundResource(R.drawable.ic_arrow)

                itemBinding.let {
                    //it.textViewCareTeamName.text = fullName
                    it.textViewCareTeamName.text = firstName + " " + lastName

                    it.imageViewCareTeam.setImageFromUrl(
                        imageUrl,
                        firstName, lastName
                    )


                    it.textViewCareTeamRole.text = careTeam.careRoles?.name
                }

                itemBinding.root.setOnClickListener {
                    recyclerItemListener.onItemSelected(
                        careTeams[position],
                        ClickType.View.value
                    )
                }
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateCareTeams(careTeams: ArrayList<CareTeamModel>) {
        this.careTeams = careTeams
        notifyDataSetChanged()
    }

}