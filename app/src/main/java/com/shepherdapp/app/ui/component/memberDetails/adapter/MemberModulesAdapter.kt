package com.shepherdapp.app.ui.component.memberDetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.dashboard.DashboardModel
import com.shepherdapp.app.databinding.AdapterMemberModulesBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.MemberDetailsViewModel


class MemberModulesAdapter(
    private val viewModel: MemberDetailsViewModel,
    var requestList: MutableList<DashboardModel> = ArrayList()
) :
    RecyclerView.Adapter<MemberModulesAdapter.MemberModulesViewHolder>() {
    lateinit var binding: AdapterMemberModulesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MemberModulesViewHolder {
        context = parent.context
        binding =
            AdapterMemberModulesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MemberModulesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: MemberModulesViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class MemberModulesViewHolder(private val itemBinding: AdapterMemberModulesBinding) :
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