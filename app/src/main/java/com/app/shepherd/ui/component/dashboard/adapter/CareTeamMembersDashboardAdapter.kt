package com.app.shepherd.ui.component.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.AdapterCareTeamMembersDashboardBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.DashboardViewModel
import com.squareup.picasso.Picasso


class CareTeamMembersDashBoardAdapter(
    private val viewModel: DashboardViewModel,
    var careTeams: MutableList<CareTeam> = ArrayList()
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
            itemBinding.data = careTeam
            val imageUrl = careTeam.user?.userProfiles?.profilePhoto

            itemBinding.let {
                Picasso.get().load(imageUrl).placeholder(R.drawable.test_image)
                    .into(it.ivCare)
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

    fun addData(careTeams: ArrayList<CareTeam>) {
        this.careTeams.clear()
        this.careTeams = careTeams
        notifyDataSetChanged()
    }


}