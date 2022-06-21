package com.app.shepherd.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.databinding.AdapterCarePointsEventsBinding
import com.app.shepherd.databinding.AdapterEventsMembersBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.ui.component.carePoints.CarePointsViewModel


class CarePointsEventAdapter(
    var requestList: MutableList<String> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsEventAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterEventsMembersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterEventsMembersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return 6
    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
       // holder.bind(position, onItemClickListener)
    }



    class CarePointsEventsViewHolder(private val itemBinding: AdapterEventsMembersBinding) :
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