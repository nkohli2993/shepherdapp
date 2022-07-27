package com.app.shepherd.ui.component.myMedList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterMyMedicationsListBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.MedListViewModel


class MyMedicationsAdapter(
    private val viewModel: MedListViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {
    lateinit var binding: AdapterMyMedicationsListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyMedicationsViewHolder {
        context = parent.context
        binding =
            AdapterMyMedicationsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyMedicationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 6
    }

    override fun onBindViewHolder(holder: MyMedicationsViewHolder, position: Int) {
        //holder.bind(requestList[position], onItemClickListener)
    }


    class MyMedicationsViewHolder(private val itemBinding: AdapterMyMedicationsListBinding) :
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