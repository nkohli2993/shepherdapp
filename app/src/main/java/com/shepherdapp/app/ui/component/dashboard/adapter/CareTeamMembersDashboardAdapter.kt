package com.shepherdapp.app.ui.component.dashboard.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.dashboard.CareTeamProfiles
import com.shepherdapp.app.databinding.AdapterCareTeamMembersDashboardBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.TextDrawable
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.DashboardViewModel
import com.squareup.picasso.Picasso


class CareTeamMembersDashBoardAdapter(
    private val viewModel: DashboardViewModel,
    var careTeams: MutableList<CareTeamProfiles> = ArrayList()
) :
    RecyclerView.Adapter<CareTeamMembersDashBoardAdapter.CareTeamViewHolder>() {
    lateinit var binding: AdapterCareTeamMembersDashboardBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openMemberDetails(itemData[0] as CareTeam)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareTeamViewHolder {
        context = parent.context
        binding =
            AdapterCareTeamMembersDashboardBinding.inflate(
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


    inner class CareTeamViewHolder(private val itemBinding: AdapterCareTeamMembersDashboardBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            val careTeam = careTeams[position]
            //itemBinding.data = careTeam
            //val imageUrl = careTeam.user?.userProfiles?.profilePhoto
            //val imageUrl = careTeam.user?.userProfiles?.profilePhoto

            val imageUrl = careTeam.user?.profilePhoto

            val firstName = careTeam.user?.firstname
            val lastName = careTeam.user?.lastname


             itemBinding.let {
                 it.ivCare.setImageFromUrl(
                     imageUrl,
                     firstName, lastName
                 )

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

    fun addData(careTeams: ArrayList<CareTeamProfiles>) {
        this.careTeams.clear()
        this.careTeams = careTeams
        notifyDataSetChanged()
    }


}