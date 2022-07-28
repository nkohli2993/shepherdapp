package com.shepherd.app.ui.component.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.databinding.AdapterCareTeamMembersDashboardBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.DashboardViewModel
import com.squareup.picasso.Picasso


class CareTeamMembersDashBoardAdapter(
    private val viewModel: DashboardViewModel,
    var careTeams: MutableList<String> = ArrayList()
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
            val imageUrl = careTeams[position]
            //itemBinding.data = careTeam
            //val imageUrl = careTeam.user?.userProfiles?.profilePhoto
            //val imageUrl = careTeam.user?.userProfiles?.profilePhoto

            itemBinding.let {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_defalut_profile_pic)
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

    fun addData(careTeams: ArrayList<String>) {
        this.careTeams.clear()
        this.careTeams = careTeams
        notifyDataSetChanged()
    }


}