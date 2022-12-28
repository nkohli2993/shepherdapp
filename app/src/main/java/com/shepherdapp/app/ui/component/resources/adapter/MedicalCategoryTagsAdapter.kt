package com.shepherdapp.app.ui.component.resources.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.CategoryData
import com.shepherdapp.app.databinding.AdapterMedicalHistoryBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.ResourceViewModel


class MedicalCategoryTagsAdapter(
    private val viewModel: ResourceViewModel,
    var requestList: MutableList<CategoryData> = ArrayList()
) :
    RecyclerView.Adapter<MedicalCategoryTagsAdapter.MedicalHistoryViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSelectedMedicalCategoryTag(itemData[0] as CategoryData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalHistoryViewHolder {
        context = parent.context
        binding =
            AdapterMedicalHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MedicalHistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: MedicalHistoryViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class MedicalHistoryViewHolder(private val itemBinding: AdapterMedicalHistoryBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(categoryData: CategoryData, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard

            // Set Category Name
            itemBinding.txtCategoryName.text = categoryData.name

            if (categoryData.isSelected) {
                itemBinding.imgCategory.setImageResource(R.drawable.ic_round_cancel)
                itemBinding.layoutCategory.setBackgroundResource(R.drawable.shape_black_border_filled)
            } else {
                itemBinding.imgCategory.setImageResource(R.drawable.ic_add_filled)
                itemBinding.layoutCategory.setBackgroundResource(R.drawable.shape_black_border)
            }

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    categoryData
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

    fun addData(categoryDataList: MutableList<CategoryData>) {
        this.requestList.clear()
        this.requestList.addAll(categoryDataList)
        notifyDataSetChanged()
    }

}