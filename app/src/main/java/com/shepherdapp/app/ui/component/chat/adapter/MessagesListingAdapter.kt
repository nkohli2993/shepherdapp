package com.shepherdapp.app.ui.component.chat.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.chat.ChatListData
import com.shepherdapp.app.data.dto.chat.ChatUserListing
import com.shepherdapp.app.databinding.AdapterAssigneeUsersBinding
import com.shepherdapp.app.utils.loadImageFromURL

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
            R.layout.adapter_assignee_users,
            parent,
            false
        ) as AdapterAssigneeUsersBinding
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = requestList[holder.bindingAdapterPosition]
        Log.e("catch_exception","userid: $userId ${data.user1?.userId}")
        if (data.user1?.userId?.toInt() == userId){
            holder.binding.textViewCareTeamName.text = data.user2?.firstname.plus(" ${data.user2?.lastname}")
            holder.binding.imageViewCareTeam.loadImageFromURL(data.user2?.profilePhoto,data.user2?.firstname,data.user2?.lastname)
        }else{
            holder.binding.textViewCareTeamName.text = data.user1?.firstname.plus(" ${data.user1?.lastname}")
            holder.binding.imageViewCareTeam.loadImageFromURL(data.user1?.profilePhoto,data.user1?.firstname,data.user1?.lastname)

        }

        holder.binding.root.setOnClickListener {
            onItemClickListener?.onItemClick(data)
        }
    }

    class ViewHolder(itemView: AdapterAssigneeUsersBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        var binding: AdapterAssigneeUsersBinding = itemView

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    fun addData(chatListData: List<ChatUserListing>) {
        requestList = chatListData
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(chatUserListing: ChatUserListing)
    }

}