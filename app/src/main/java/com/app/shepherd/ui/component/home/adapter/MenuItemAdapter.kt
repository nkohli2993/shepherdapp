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
import com.app.shepherd.databinding.AdapterMenuItemBinding
import com.app.shepherd.ui.component.dashboard.DashboardViewModel


class MenuItemAdapter(
    var requestList: MutableList<MenuItemModel> = ArrayList(),
    var menuItemMap: HashMap<String, ArrayList<MenuItemModel>> = HashMap()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding: AdapterMenuItemBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_menu_item,
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
            is ContentViewHolder -> holder.bind(requestList[position])
        }
    }

    inner class ContentViewHolder constructor(private var binding: AdapterMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MenuItemModel) {
            binding.data = data
            if (menuItemMap[requestList[bindingAdapterPosition].title]!=null
                &&menuItemMap[requestList[bindingAdapterPosition].title]?.size!! > 0) {
                setSubMenuItemAdapter(menuItemMap[requestList[bindingAdapterPosition].title])
            }
        }

    }

    private fun setSubMenuItemAdapter(arrayList: java.util.ArrayList<MenuItemModel>?) {
        binding.recyclerViewSubMenu.adapter = SubMenuItemAdapter(arrayList!!)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}