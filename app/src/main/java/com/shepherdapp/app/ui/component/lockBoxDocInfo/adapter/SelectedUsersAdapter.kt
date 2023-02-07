package com.shepherdapp.app.ui.component.lockBoxDocInfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AllowedUsers
import com.shepherdapp.app.databinding.AdapterCareTeamMembersBinding
import com.shepherdapp.app.databinding.AdapterSelectUsersBinding
import com.shepherdapp.app.databinding.AdapterSelectedUsersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.view_model.CareTeamMembersViewModel
import com.shepherdapp.app.view_model.LockBoxDocInfoViewModel
import com.shepherdapp.app.view_model.SelectUsersViewModel
import com.squareup.picasso.Picasso


class SelectedUsersAdapter(
    private val viewModel: LockBoxDocInfoViewModel,
    var allowedUsers: MutableList<AllowedUsers> = ArrayList()

) :
    RecyclerView.Adapter<SelectedUsersAdapter.CareTeamViewHolder>() {
    lateinit var binding: AdapterSelectedUsersBinding
    lateinit var context: Context


    /* private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
         override fun onItemSelected(vararg itemData: Any) {
             viewModel.selectedPosition(itemData[0] as Int)
         }
     }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CareTeamViewHolder {
        context = parent.context
        binding =
            AdapterSelectedUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CareTeamViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return allowedUsers.size
    }

    override fun onBindViewHolder(holder: CareTeamViewHolder, position: Int) {
        holder.bind(position/*, onItemClickListener*/)
    }


    inner class CareTeamViewHolder(private val itemBinding: AdapterSelectedUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int/*, recyclerItemListener: RecyclerItemListener*/) {
            val allowedUser = allowedUsers[position]
            val imageUrl = allowedUser.userProfiles?.profilePhoto
            val fullName =
                allowedUser.userProfiles?.firstname + " " + allowedUser.userProfiles?.lastname

            itemBinding.let {
                it.txtUser.text = fullName
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imageViewCareTeam)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateUserList(allowedUsers: ArrayList<AllowedUsers>) {
        this.allowedUsers = allowedUsers
        notifyDataSetChanged()
    }

}