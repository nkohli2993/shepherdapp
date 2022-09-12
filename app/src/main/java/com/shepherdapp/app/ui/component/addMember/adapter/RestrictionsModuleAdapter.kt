package com.shepherdapp.app.ui.component.addMember.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.dashboard.DashboardModel
import com.shepherdapp.app.databinding.AdapterRestrictionModulesBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.AddMemberViewModel


class RestrictionsModuleAdapter(
    private val viewModel: AddMemberViewModel,
    var requestList: MutableList<DashboardModel> = ArrayList()
) :
    RecyclerView.Adapter<RestrictionsModuleAdapter.RestrictionModulesViewHolder>() {
    lateinit var binding: AdapterRestrictionModulesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestrictionModulesViewHolder {
        context = parent.context
        binding =
            AdapterRestrictionModulesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RestrictionModulesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: RestrictionModulesViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class RestrictionModulesViewHolder(private val itemBinding: AdapterRestrictionModulesBinding) :
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