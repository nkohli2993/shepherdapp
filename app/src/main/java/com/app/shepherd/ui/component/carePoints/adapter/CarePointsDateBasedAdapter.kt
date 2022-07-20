package com.app.shepherd.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.added_events.AddedEventModel
import com.app.shepherd.data.dto.added_events.EventCommentsModel
import com.app.shepherd.databinding.AdapterCarePointsDayBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import java.text.SimpleDateFormat

class CarePointsDateBasedAdapter(
    val viewModel: CreatedCarePointsViewModel,
    var carePointList: MutableList<AddedEventModel> = ArrayList(),
    val listener: OnCarePointSelected
) :
    RecyclerView.Adapter<CarePointsDateBasedAdapter.CarePointsDayViewHolder>() {
    lateinit var binding: AdapterCarePointsDayBinding
    lateinit var context: Context
  /*  private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openEventChat(itemData[0] as Int)

        }
    }*/
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
        //  return carePointList.size
        return carePointList.size
    }

    override fun onBindViewHolder(holder: CarePointsDayViewHolder, position: Int) {
        holder.bind(position)

    }

    private fun setCarePointsAdapter(
        recyclerViewEvents: RecyclerView,
        eventComments: ArrayList<EventCommentsModel>
    ) {
        val carePointsEventAdapter = CarePointsEventAdapter(eventComments)
        recyclerViewEvents.adapter = carePointsEventAdapter
    }

    inner class CarePointsDayViewHolder(private val itemBinding: AdapterCarePointsDayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(position: Int) {
            val carePoints = carePointList[position]
            itemBinding.data = carePoints
            if(carePoints.time!=null){
                val carePointDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                    carePoints.date.plus(" ").plus(carePoints.time?.replace(" ",""))
                )
                itemBinding.timeTV.text = SimpleDateFormat("hh:mm a").format(carePointDate!!)
            }
            itemBinding.root.setOnClickListener {
                listener.selectedCarePoint( carePointList[position].id!!)
//                recyclerItemListener.onItemSelected(
//                    carePointList[position].id!!
//                )
            }
            setCarePointsAdapter(binding.recyclerViewEvents, carePoints.event_comments)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnCarePointSelected{
        fun selectedCarePoint(id:Int)
    }
}