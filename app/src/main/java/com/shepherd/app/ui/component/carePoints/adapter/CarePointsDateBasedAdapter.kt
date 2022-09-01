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
            if (carePoints.time != null) {
                val carePointDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                    carePoints.date.plus(" ").plus(carePoints.time?.replace(" ", ""))
                )
                itemBinding.timeTV.text = SimpleDateFormat("hh:mm a").format(carePointDate!!)
            }
            itemBinding.root.setOnClickListener {
                listener.selectedCarePoint(carePointList[position])
            }
            itemBinding.view.visibility = View.VISIBLE
            if (position + 1 == carePointList.size) {
                itemBinding.view.visibility = View.GONE
            }
            //show assigns in event
            itemBinding.assigneCountTV.visibility = View.VISIBLE
            if (carePoints.user_assignes.size > 3) {
                itemBinding.assigneCountTV.visibility = View.VISIBLE
                itemBinding.assigneCountTV.text = "+${carePoints.user_assignes.size - 3}"
            }
            //check assignee and remove chat multiple
            itemBinding.ivMessage.visibility = View.VISIBLE

            when (carePoints.user_assignes.size) {
                1 -> {
                    // Check if the loggedIn user is the only assignee of the event
                    // Make the visibility of chat icon gone
                    if (isListContainMethod(carePoints.user_assignes)) {
                        if (viewModel.getUserDetail()?.userId.toString() == carePoints.created_by) {
                            itemBinding.ivMessage.visibility = View.GONE
                        } else {
                            itemBinding.ivMessage.visibility = View.VISIBLE
                        }
//                        itemBinding.ivMessage.visibility = View.VISIBLE
                    } else if (viewModel.getUserDetail()?.userId.toString() == carePoints.created_by) {
                        // Check if the loggedIn user is the assigner
                        // It means two user are there for the care point(event) ,one is assignee and other is the assigner,
                        // make the visibility of chat icon Visible
                        itemBinding.ivMessage.visibility = View.VISIBLE
                    } else {
                        // If the loggedIn User is neither the assigner nor assignee
                        itemBinding.ivMessage.visibility = View.GONE
                    }
                }
                else -> {
//                    itemBinding.ivMessage.visibility = View.VISIBLE

                    // Chat icon is visible if the loggedIn user is one of the assignee of the event or loggedIn user is the assigner
                    if (isListContainMethod(carePoints.user_assignes) || (viewModel.getUserDetail()?.userId.toString() == carePoints.created_by)) {
                        itemBinding.ivMessage.visibility = View.VISIBLE
                    } else {
                        // If the loggedIn User is neither the assigner nor assignee
                        itemBinding.ivMessage.visibility = View.GONE
                    }
                }
            }
            setCarePointsAdapter(binding.recyclerViewEvents, carePoints.user_assignes)
        }
    }

    fun isListContainMethod(arraylist: ArrayList<UserAssigneeModel>): Boolean {
        for (str in arraylist) {
            if (str.user_details.id == viewModel.getUserDetail()?.userId!!) {
                return true
            }
        }
        return false
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OnCarePointSelected {
        fun selectedCarePoint(detail: AddedEventModel)
    }
}