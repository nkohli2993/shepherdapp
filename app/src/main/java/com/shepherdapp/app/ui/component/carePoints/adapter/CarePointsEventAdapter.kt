package com.shepherdapp.app.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.databinding.AdapterAssigneeMenrbersBinding
import com.shepherdapp.app.utils.setImageFromUrl


class CarePointsEventAdapter(
    var commentList: ArrayList<UserAssigneeModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsEventAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterAssigneeMenrbersBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterAssigneeMenrbersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return if(commentList.size>3) 3 else commentList.size
    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterAssigneeMenrbersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            // Set the margin end as zero for the last item
            if (position == commentList.size - 1) {
                itemBinding.layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.marginEnd = 0
                }
            }
            if(commentList.size>3 && position == 2){
                itemBinding.layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.marginEnd = 0
                }
            }
            val imageUrl = commentList[position].user_details.profilePhoto ?: ""
            val firstName = commentList[position].user_details.firstname
            val lastName = commentList[position].user_details.lastname

            itemBinding.imageView.setImageFromUrl(imageUrl, firstName!!, lastName!!)
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}