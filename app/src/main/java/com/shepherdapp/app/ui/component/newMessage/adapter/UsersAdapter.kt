package com.shepherdapp.app.ui.component.newMessage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterUsersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.NewMessageViewModel


class UsersAdapter(
    private val viewModel: NewMessageViewModel,
    var careTeams: MutableList<CareTeamModel> = ArrayList()
) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    lateinit var binding: AdapterUsersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openChat(itemData[0] as CareTeamModel)
        }
    }

    private val onItemCheckedListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.selectUser(itemData[0] as CareTeamModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        context = parent.context
        binding =
            AdapterUsersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UsersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return careTeams.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(careTeams[position], onItemClickListener, onItemCheckedListener)
    }


    class UsersViewHolder(private val itemBinding: AdapterUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(
            careTeam: CareTeamModel,
            recyclerItemListener: RecyclerItemListener,
            recyclerViewItemCheckedListener: RecyclerItemListener
        ) {
            itemBinding.data = careTeam
            if (careTeam.isSelected == true) {
                itemBinding.chkUser.visibility = View.VISIBLE
            } else {
                itemBinding.chkUser.visibility = View.GONE
            }

            itemBinding.chkUser.setOnCheckedChangeListener { compoundButton, _isChecked ->
                if (_isChecked) {
                    careTeam.isSelected = true
                    recyclerViewItemCheckedListener.onItemSelected(careTeam)
                } else {
                    careTeam.isSelected = false
                }
            }

            // Click User
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    careTeam
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

    fun addData(careTeams: ArrayList<CareTeamModel>) {
        this.careTeams = careTeams
        notifyDataSetChanged()
    }

    fun selectUnselect(isSelected: Boolean) {
        this.careTeams.map {
            it.isSelected = isSelected
        }
        notifyDataSetChanged()
    }
}