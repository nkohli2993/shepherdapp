package com.shepherd.app.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.added_events.AddedEventModel
import com.shepherd.app.data.dto.added_events.ResultEventModel
import com.shepherd.app.databinding.AdapterCarePointsDayDateBasedBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.CreatedCarePointsViewModel
import java.text.SimpleDateFormat


class CarePointsDayAdapter(
    val viewModel: CreatedCarePointsViewModel,
    var carePointList: MutableList<ResultEventModel> = java.util.ArrayList(),
    val clickType: Int,
    val listener: EventSelected,
) :
    RecyclerView.Adapter<CarePointsDayAdapter.CarePointsDayViewHolder>(),
    CarePointsDateBasedAdapter.OnCarePointSelected {
    lateinit var binding: AdapterCarePointsDayDateBasedBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openEventChat(itemData[0] as Int)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsDayViewHolder {
        context = parent.context
        binding =
            AdapterCarePointsDayDateBasedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsDayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return carePointList.size
        return carePointList.size
    }

    override fun onBindViewHolder(holder: CarePointsDayViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun setCarePointsAdapter(
        recyclerViewEvents: RecyclerView,
        events: ArrayList<AddedEventModel>
    ) {
        val carePointsEventAdapter =
            CarePointsDateBasedAdapter(viewModel, events,this)
        recyclerViewEvents.adapter = carePointsEventAdapter
    }

    inner class CarePointsDayViewHolder(private val itemBinding: AdapterCarePointsDayDateBasedBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(position: Int) {
            val carePoints = carePointList[position]
            val dateShow = SimpleDateFormat("yyyy-MM-dd").parse(carePoints.date!!)
            itemBinding.dateTV.text = SimpleDateFormat("EEE, MMM dd").format(dateShow!!)

            setCarePointsAdapter(binding.recyclerViewEventDays, carePoints.events)

        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun updateCarePoints(careTeams: ArrayList<ResultEventModel>) {
        this.carePointList = careTeams
        notifyDataSetChanged()
    }

    override fun selectedCarePoint(id: Int) {
        listener.onEventSelected(id)
    }

    interface  EventSelected{
        fun onEventSelected(id:Int)
    }
}