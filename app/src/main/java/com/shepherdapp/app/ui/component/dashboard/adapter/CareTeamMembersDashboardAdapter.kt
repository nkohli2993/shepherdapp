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
            val first = firstName?.first().toString()
            var last: String? = null
            var fullName: String? = null
            if (lastName != null) {
                last = lastName.first().toString()
                fullName = "$first$last"
            } else {
                fullName = first
            }

           /* itemBinding.let {
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.icon_default_profile_pic)
                        .into(it.ivCare)
                } else {
                    val drawable = TextDrawable.builder()
                        .beginConfig()
                        .textColor(Color.RED)
                        .useFont(Typeface.DEFAULT)
                        .endConfig()
                        .buildRect(fullName, ContextCompat.getColor(context, R.color.colorPurple))

                    it.ivCare.setImageDrawable(drawable)
                }
            }*/


             itemBinding.let {
                 if (imageUrl != null && imageUrl != "") {
                     Picasso.get().load(imageUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                         .into(it.ivCare)
                 }
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