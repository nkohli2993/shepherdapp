package com.app.shepherd.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterRecommendedDocumentsBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.LockBoxViewModel


class RecommendedDocumentsAdapter(
    private val viewModel: LockBoxViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<RecommendedDocumentsAdapter.RecommendedDocumentsViewHolder>() {
    lateinit var binding: AdapterRecommendedDocumentsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedDocumentsViewHolder {
        context = parent.context
        binding =
            AdapterRecommendedDocumentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecommendedDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: RecommendedDocumentsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class RecommendedDocumentsViewHolder(private val itemBinding: AdapterRecommendedDocumentsBinding) :
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