package com.shepherd.app.ui.component.newMessage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.databinding.AdapterUsersBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.NewMessageViewModel


class UsersAdapter(
    private val viewModel: NewMessageViewModel,
    var careTeams: MutableList<CareTeamModel> = ArrayList()
) :
    RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    lateinit var binding: AdapterUsersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
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
        holder.bind(careTeams[position], onItemClickListener)
    }


    class UsersViewHolder(private val itemBinding: AdapterUsersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(careTeam: CareTeamModel, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = careTeam
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

}