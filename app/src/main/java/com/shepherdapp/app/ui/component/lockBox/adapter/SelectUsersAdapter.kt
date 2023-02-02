package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterCareTeamMembersBinding
import com.shepherdapp.app.databinding.AdapterSelectUsersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.ClickType
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.margin
import com.shepherdapp.app.view_model.CareTeamMembersViewModel
import com.shepherdapp.app.view_model.SelectUsersViewModel
import com.squareup.picasso.Picasso


class SelectUsersAdapter(
    private val viewModel: SelectUsersViewModel,
    var careTeams: MutableList<CareTeamModel> = ArrayList()

) :
    RecyclerView.Adapter<SelectUsersAdapter.CareTeamViewHolder>() {
    lateinit var binding: AdapterSelectUsersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.selectedPosition(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareTeamViewHolder {
        context = parent.context
        binding =
            AdapterSelectUsersBinding.inflate(
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


    inner class CareTeamViewHolder(private val itemBinding: AdapterSelectUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            val careTeam = careTeams[position]
            val firstName = careTeam.user_id_details.firstname
            val lastName = careTeam.user_id_details.lastname
            val fullName = "$firstName $lastName"
            val imageUrl = careTeam.user_id_details.profilePhoto

            itemBinding.let {
                it.txtUser.text = fullName
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imageViewCareTeam)
            }

            itemBinding.chkUsers.setOnCheckedChangeListener { compoundButton, b ->
                recyclerItemListener.onItemSelected(position)
            }

            /*  itemBinding.root.setOnClickListener {
                  recyclerItemListener.onItemSelected(
                      careTeams[position],
                      ClickType.View.value
                  )

              }*/
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