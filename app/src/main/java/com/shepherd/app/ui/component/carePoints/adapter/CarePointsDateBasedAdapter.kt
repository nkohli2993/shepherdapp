package com.shepherd.app.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.added_events.AddedEventModel
import com.shepherd.app.data.dto.added_events.UserAssigneeModel
import com.shepherd.app.databinding.AdapterCarePointsDayBinding
import com.shepherd.app.view_model.CreatedCarePointsViewModel
import java.text.SimpleDateFormat

class CarePointsDateBasedAdapter(
    val viewModel: CreatedCarePointsViewModel,
    var carePointList: MutableList<AddedEventModel> = ArrayList(),
    val listener: OnCarePointSelected,
) :
    RecyclerView.Adapter<CarePointsDateBasedAdapter.CarePointsDayViewHolder>() {
    lateinit var binding: AdapterCarePointsDayBinding
    lateinit var context: Context

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
        eventComments: ArrayList<UserAssigneeModel>
    ) {
        val carePointsEventAdapter = CarePointsListEventAdapter(eventComments)
        recyclerViewEvents.adapter = carePointsEventAdapter
    }

    inner class CarePointsDayViewHolder(private val itemBinding: AdapterCarePointsDayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
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
            }
            itemBinding.view.visibility = View.VISIBLE
            if(position+1 == carePointList.size){
                itemBinding.view.visibility = View.GONE
            }
            //show assigns in event
            itemBinding.assigneCountTV.visibility = View.VISIBLE
            if(carePoints.user_assignes.size>3){
                itemBinding.assigneCountTV.visibility = View.VISIBLE
                itemBinding.assigneCountTV.text = "+${carePoints.user_assignes.size-3}"
            }
            setCarePointsAdapter(binding.recyclerViewEvents, carePoints.user_assignes)
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