package com.shepherdapp.app.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.AddedEventModel
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.databinding.AdapterCarePointsDayBinding
import com.shepherdapp.app.utils.ClickType
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
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

            // Disable edit icon if the loggedIn User is not the creator of the event
            val uuidEventCreator = carePoints.createdByDetails?.uid

            // Get UUID of loggedInUser
            val loggedInUserUUID = Prefs.with(ShepherdApp.appContext)?.getString(Const.UUID, "")
            if (loggedInUserUUID == uuidEventCreator) {
                itemBinding.imgEditCarePoint.visibility = View.VISIBLE
            } else {
                itemBinding.imgEditCarePoint.visibility = View.GONE
            }


            if (carePoints.time != null) {
                val carePointDate =
                    if (carePoints.time!!.contains("am") || carePoints.time!!.contains("AM") || carePoints.time!!.contains("pm") || carePoints.time!!.contains("PM")) {
                        SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(
                            carePoints.date.plus(" ").plus(carePoints.time)
                        )
                    } else {
                        SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                            carePoints.date.plus(" ").plus(carePoints.time)
                        )
                    }
                itemBinding.timeTV.text = SimpleDateFormat("hh:mm a").format(carePointDate!!)
            }
            // View CarePoint
            itemBinding.root.setOnClickListener {
                val carePoint = carePointList[position]
                carePoint.clickType = ClickType.View.value
                listener.selectedCarePoint(carePoint)
            }
            // Edit Care Point
            itemBinding.imgEditCarePoint.setOnClickListener {
                val carePoint = carePointList[position]
                carePoint.clickType = ClickType.Edit.value
                listener.selectedCarePoint(carePoint)
            }
            itemBinding.txtMoreAssignee.setOnClickListener {
                val carePoint = carePointList[position]
                carePoint.clickType = ClickType.ASSIGNEE.value
                listener.selectedCarePoint(carePoint)
            }
            itemBinding.recyclerViewEvents.setOnClickListener {
                val carePoint = carePointList[position]
                carePoint.clickType = ClickType.ASSIGNEE.value
                listener.selectedCarePoint(carePoint)
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
                    if (isListContainMethod(carePoints.user_assignes)) {
                        if (viewModel.getUserDetail()?.userId == carePoints.createdByDetails?.id) {
                            itemBinding.ivMessage.visibility = View.GONE
                        } else {
                            itemBinding.ivMessage.visibility = View.VISIBLE
                        }
                    } else if (viewModel.getUserDetail()?.userId == carePoints.createdByDetails?.id) {
                        itemBinding.ivMessage.visibility = View.VISIBLE
                    } else {
                        // If the loggedIn User is neither the assigner nor assignee
                        itemBinding.ivMessage.visibility = View.GONE
                    }
                }
                else -> {
                    if (isListContainMethod(carePoints.user_assignes) || (viewModel.getUserDetail()?.userId == carePoints.createdByDetails?.id)) {
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