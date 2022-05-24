package com.app.shepherd.ui.component.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.data.dto.menuItem.MenuItemModel
import com.app.shepherd.databinding.AdapterDashboardBinding
import com.app.shepherd.databinding.AdapterSubMenuItemBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.dashboard.DashboardViewModel
import com.app.shepherd.ui.component.home.viewModel.HomeViewModel


class SubMenuItemAdapter(
    var viewModel: HomeViewModel,
    var requestList: MutableList<MenuItemModel> = ArrayList()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding: AdapterSubMenuItemBinding
    lateinit var context: Context

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
             viewModel.onDrawerItemSelected(itemData[0] as String)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_sub_menu_item,
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContentViewHolder -> holder.bind(requestList[position],onItemClickListener)
        }
    }

    inner class ContentViewHolder constructor(private var binding: AdapterSubMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MenuItemModel, recyclerItemListener: RecyclerItemListener) {
            binding.data = data

            binding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    data.title
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

    fun addData(dashboard: MutableList<MenuItemModel>) {
        this.requestList.clear()
        this.requestList.addAll(dashboard)
        notifyDataSetChanged()
    }

}