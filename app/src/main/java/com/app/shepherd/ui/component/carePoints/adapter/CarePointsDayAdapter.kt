package com.app.shepherd.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.databinding.AdapterCarePointsDayBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.carePoints.CarePointsViewModel


class CarePointsDayAdapter(
    val viewModel: CarePointsViewModel,
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsDayAdapter.CarePointsDayViewHolder>() {
    lateinit var binding: AdapterCarePointsDayBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsDayViewHolder {
        context = parent.context
        binding =
            AdapterCarePointsDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsDayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 6
    }

    override fun onBindViewHolder(holder: CarePointsDayViewHolder, position: Int) {
       // holder.bind(position, onItemClickListener)
        setCarePointsAdapter(binding.recyclerViewEvents)
    }

    private fun setCarePointsAdapter(recyclerViewEvents: RecyclerView) {
        val carePointsEventAdapter = CarePointsEventAdapter()
        recyclerViewEvents.adapter = carePointsEventAdapter
    }


    class CarePointsDayViewHolder(private val itemBinding: AdapterCarePointsDayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
           // itemBinding.data = dashboard
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    position
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