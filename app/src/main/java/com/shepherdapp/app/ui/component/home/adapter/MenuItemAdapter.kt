package com.shepherdapp.app.ui.component.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.menuItem.MenuItemModel
import com.shepherdapp.app.databinding.AdapterMenuItemBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.HomeViewModel


class MenuItemAdapter(
    var viewModel: HomeViewModel,
    var requestList: MutableList<MenuItemModel> = ArrayList(),
    var menuItemMap: HashMap<String, ArrayList<MenuItemModel>> = HashMap()
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var binding: AdapterMenuItemBinding
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
            is ContentViewHolder -> holder.bind(requestList[position],onItemClickListener)
        }
    }

    inner class ContentViewHolder constructor(private var binding: AdapterMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: MenuItemModel, recyclerItemListener: RecyclerItemListener) {
            binding.data = data
            if (menuItemMap[requestList[bindingAdapterPosition].title]!=null
                &&menuItemMap[requestList[bindingAdapterPosition].title]?.size!! > 0) {
                setSubMenuItemAdapter(menuItemMap[requestList[bindingAdapterPosition].title])
            }

            binding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    data.title
                )
            }

        }

    }

    private fun setSubMenuItemAdapter(arrayList: java.util.ArrayList<MenuItemModel>?) {
        binding.recyclerViewSubMenu.adapter = SubMenuItemAdapter(viewModel,arrayList!!)
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}