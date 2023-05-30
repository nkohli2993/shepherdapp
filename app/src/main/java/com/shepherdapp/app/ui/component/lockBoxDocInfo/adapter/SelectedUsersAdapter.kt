package com.shepherdapp.app.ui.component.lockBoxDocInfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AllowedUsers
import com.shepherdapp.app.databinding.AdapterSelectedUsersBinding
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.view_model.LockBoxDocInfoViewModel


class SelectedUsersAdapter(
    private val viewModel: LockBoxDocInfoViewModel,
    var listener :SelectedUser,
    var allowedUsers: MutableList<AllowedUsers> = ArrayList()
) :
    RecyclerView.Adapter<SelectedUsersAdapter.CareTeamViewHolder>() {
    lateinit var binding: AdapterSelectedUsersBinding
    lateinit var context: Context


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
        holder.bind(position,listener)
    }


    inner class CareTeamViewHolder(private val itemBinding: AdapterSelectedUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(
            position: Int,
            listener: SelectedUser
        ) {
            val allowedUser = allowedUsers[position]
            val imageUrl = allowedUser.userProfiles?.profilePhoto
            val fullName =
                allowedUser.userProfiles?.firstname + " " + allowedUser.userProfiles?.lastname

            itemBinding.let {
                it.txtUser.text = fullName

                it.imageViewCareTeam.setImageFromUrl(
                    imageUrl,
                    allowedUser.userProfiles?.firstname,
                    allowedUser.userProfiles?.lastname
                )

                it.root.setOnClickListener {
                    listener.onSelectedUserClick(allowedUser)
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

    fun updateUserList(allowedUsers: ArrayList<AllowedUsers>) {
        this.allowedUsers = allowedUsers
        notifyDataSetChanged()
    }

    interface SelectedUser{
        fun onSelectedUserClick(user:AllowedUsers)
    }
}