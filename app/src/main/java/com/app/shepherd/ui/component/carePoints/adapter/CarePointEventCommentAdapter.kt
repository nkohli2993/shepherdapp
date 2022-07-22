package com.app.shepherd.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.added_events.EventCommentUserDetailModel
import com.app.shepherd.databinding.AdapterCommentBinding
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CarePointEventCommentAdapter(
    var commentList: ArrayList<EventCommentUserDetailModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointEventCommentAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterCommentBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterCommentBinding.inflate(
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


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterCommentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat")
        fun bind(position: Int) {
            val commentData = commentList[position]

            itemBinding.let {
                Picasso.get().load(commentData.user_details.user_profiles.profilePhoto)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imageViewUser)
                it.tvUsername.text = commentData.user_details.user_profiles.firstname.plus(" ")
                    .plus(commentData.user_details.user_profiles.lastname)
                it.appCompatTextViewMessage.text = commentData.comment
                //setDate of comment
                val dateTime = (commentData.created_at?:"").replace(".000Z","").replace("T"," ")
                val commentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    dateTime
                )
                val df =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                df.timeZone = TimeZone.getTimeZone("UTC")
                val date: Date = df.parse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commentTime!!))!!
                df.timeZone = TimeZone.getDefault()
                it.tvtime.text = SimpleDateFormat("hh:mm a").format(date);
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