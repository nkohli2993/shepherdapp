package com.shepherd.app.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.dashboard.DashboardModel
import com.shepherd.app.databinding.AdapterMyRemindersBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.ui.component.myMedList.MyMedListViewModel


class MyRemindersAdapter(
    private val viewModel: MyMedListViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<MyRemindersAdapter.MyRemindersViewHolder>() {
    lateinit var binding: AdapterMyRemindersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyRemindersViewHolder {
        context = parent.context
        binding =
            AdapterMyRemindersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyRemindersViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 20
    }

    override fun onBindViewHolder(holder: MyRemindersViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class MyRemindersViewHolder(private val itemBinding: AdapterMyRemindersBinding) :
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