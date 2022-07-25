package com.app.shepherd.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterOtherDocumentsBinding
import com.app.shepherd.databinding.AdapterUploadedFilesBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.AddNewLockBoxViewModel


class UploadedFilesAdapter(
    private val viewModel: AddNewLockBoxViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<UploadedFilesAdapter.UploadedDocumentsViewHolder>() {
    lateinit var binding: AdapterUploadedFilesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UploadedDocumentsViewHolder {
        context = parent.context
        binding =
            AdapterUploadedFilesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return UploadedDocumentsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 4
    }

    override fun onBindViewHolder(holder: UploadedDocumentsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class UploadedDocumentsViewHolder(private val itemBinding: AdapterUploadedFilesBinding) :
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