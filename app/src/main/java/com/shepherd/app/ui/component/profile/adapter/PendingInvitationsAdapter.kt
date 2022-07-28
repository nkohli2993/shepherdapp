package com.shepherd.app.ui.component.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.databinding.AdapterPendingInvitationsBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.ProfileViewModel


class PendingInvitationsAdapter(
    private val viewModel: ProfileViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<PendingInvitationsAdapter.PendingInvitationsViewHolder>() {
    lateinit var binding: AdapterPendingInvitationsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingInvitationsViewHolder {
        context = parent.context
        binding =
            AdapterPendingInvitationsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return PendingInvitationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 1
    }

    override fun onBindViewHolder(holder: PendingInvitationsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class PendingInvitationsViewHolder(private val itemBinding: AdapterPendingInvitationsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(dashboard: DashboardModel, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    dashboard
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

    fun addData(dashboard: MutableList<String>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}