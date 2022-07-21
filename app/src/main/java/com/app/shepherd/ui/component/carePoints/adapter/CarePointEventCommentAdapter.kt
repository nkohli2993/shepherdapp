package com.app.shepherd.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.added_events.EventCommentUserDetailModel
import com.app.shepherd.databinding.AdapterChatBinding

class CarePointEventCommentAdapter(
    var commentList: ArrayList<EventCommentUserDetailModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointEventCommentAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterChatBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return commentList.size

    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int) {
            val commentData = commentList[position]

            itemBinding.let {
               it.appCompatTextViewMessage.text = commentData.comment
                //setDate of comment
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    fun updateAddedComment(commentAddedList: ArrayList<EventCommentUserDetailModel>) {
        this.commentList = commentAddedList
        notifyDataSetChanged()
    }


}