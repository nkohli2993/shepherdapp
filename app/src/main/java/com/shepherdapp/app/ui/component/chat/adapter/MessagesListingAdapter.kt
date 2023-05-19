package com.shepherdapp.app.ui.component.chat.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.chat.ChatUserListing
import com.shepherdapp.app.databinding.AdapterDirectMessagesBinding
import com.shepherdapp.app.utils.loadImageFromURL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessagesListingAdapter(
    var requestList: List<ChatUserListing> = ArrayList(),
    var userId: Int? = null,
    var onItemClickListener: OnItemClickListener? = null
) :
    RecyclerView.Adapter<MessagesListingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_direct_messages,
            parent,
            false
        ) as AdapterDirectMessagesBinding
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun timeStampToDateFromUTC(timeStamp: Long): String {
        val sdf = SimpleDateFormat("MM-dd-yyyy hh:mm a")
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val utcDate =
            if (timeStamp.toString().length == 13)
                Date(timeStamp)
            else Date(timeStamp * 1000)

        val dateFormatter = SimpleDateFormat("MMM dd,yyyy") //this format changeable
        dateFormatter.timeZone = TimeZone.getDefault()

        val timeFormatter = SimpleDateFormat("hh:mm a") //this format changeable
        timeFormatter.timeZone = TimeZone.getDefault()

        val currentDate = dateFormatter.format(Calendar.getInstance().time)
        val date = dateFormatter.parse(currentDate)
        val messageDate = dateFormatter.parse(dateFormatter.format(utcDate))
        return if (messageDate!! == date) {
            timeFormatter.format(utcDate)
        } else {
            dateFormatter.format(utcDate)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = requestList[holder.bindingAdapterPosition]
        if (data.user1?.userId?.toInt() == userId) {
            holder.binding.txtName.text = data.user2?.firstname.plus(" ${data.user2?.lastname}")
            holder.binding.imgChatUser.loadImageFromURL(
                data.user2?.profilePhoto,
                data.user2?.firstname,
                data.user2?.lastname
            )
        } else {
            holder.binding.txtName.text = data.user1?.firstname.plus(" ${data.user1?.lastname}")
            holder.binding.imgChatUser.loadImageFromURL(
                data.user1?.profilePhoto,
                data.user1?.firstname,
                data.user1?.lastname
            )

        }
        holder.binding.txtMessage.text = data.lastMessages

        val cal = Calendar.getInstance()
        cal.time = data.createdAt!!.toDate()
        holder.binding.txtTime.text = timeStampToDateFromUTC(cal.timeInMillis)
        if (((data.lastSenderId ?: "0").toInt()) != userId) {
            Log.e("catch_id", "if")
            holder.binding.txtUnreadCount.text = data.unseenMessageCount.toString()
            if (data.unseenMessageCount!! > 0)
                holder.binding.txtUnreadCount.visibility = View.VISIBLE
            else
                holder.binding.txtUnreadCount.visibility = View.GONE

        } else {
            Log.e("catch_id", "else")
            holder.binding.txtUnreadCount.visibility = View.GONE
        }

        holder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(data)
        }
    }

    class ViewHolder(itemView: AdapterDirectMessagesBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: AdapterDirectMessagesBinding = itemView

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addData(chatListData: List<ChatUserListing>) {
        requestList = chatListData
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(chatUserListing: ChatUserListing)
    }

}