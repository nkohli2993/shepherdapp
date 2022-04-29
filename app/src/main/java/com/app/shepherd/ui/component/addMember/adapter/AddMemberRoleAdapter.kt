package com.app.shepherd.ui.component.addMember.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterAddMemberRoleBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.addMember.AddMemberViewModel


class AddMemberRoleAdapter(
    private val viewModel: AddMemberViewModel,
    var requestList: MutableList<DashboardModel> = ArrayList()
) :
    RecyclerView.Adapter<AddMemberRoleAdapter.AddMemberRoleViewHolder>() {
    lateinit var binding: AdapterAddMemberRoleBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
           // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMemberRoleViewHolder {
        context = parent.context
        binding =
            AdapterAddMemberRoleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AddMemberRoleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 6
    }

    override fun onBindViewHolder(holder: AddMemberRoleViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class AddMemberRoleViewHolder(private val itemBinding: AdapterAddMemberRoleBinding) :
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

    fun addData(dashboard: MutableList<DashboardModel>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}