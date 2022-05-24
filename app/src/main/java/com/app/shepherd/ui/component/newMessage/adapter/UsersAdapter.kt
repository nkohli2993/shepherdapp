package com.app.shepherd.ui.component.newMessage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterUsersBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.messages.MessagesViewModel
import com.app.shepherd.ui.component.newMessage.NewMessageViewModel


class UsersAdapter(
    private val viewModel: NewMessageViewModel,
    var requestList: MutableList<String> = ArrayList()
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
        //  return requestList.size
        return 12
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class UsersViewHolder(private val itemBinding: AdapterUsersBinding) :
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