package com.shepherdapp.app.ui.component.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.chat.ChatListData
import com.shepherdapp.app.databinding.AdapterAssigneeUsersBinding

class MessagesListingAdapter(
    var requestList: ArrayList<ChatListData?>,
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


/*
        if (requestList[holder.bindingAdapterPosition].user1?.id?.toInt() == userId){
            holder.binding.textViewCareTeamName.text = requestList[holder.bindingAdapterPosition].user2?.name
            holder.binding.imageViewCareTeam.loadImageFromURL(requestList[holder.bindingAdapterPosition].user2?.imageUrl,requestList[holder.bindingAdapterPosition].user2?.name,"")
        }else{
            holder.binding.textViewCareTeamName.text = requestList[holder.bindingAdapterPosition].user1?.name
            holder.binding.imageViewCareTeam.loadImageFromURL(requestList[holder.bindingAdapterPosition].user1?.imageUrl,requestList[holder.bindingAdapterPosition].user2?.name,"")

        }
*/

//        holder.binding.textViewCareTeamName.text = requestList[holder.bindingAdapterPosition].name
//        holder.binding.imageViewCareTeam.loadImageFromURL(requestList[holder.bindingAdapterPosition].user2?.imageUrl,requestList[holder.bindingAdapterPosition].user2?.name,"")
//        holder.itemView.setOnClickListener {
//            onItemClickListener?.onItemClick(requestList[holder.bindingAdapterPosition])
//        }
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



    fun addData(chatListData: ArrayList<ChatListData?>) {
        this.requestList.clear()
        this.requestList.addAll(chatListData)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(chatUserListing: ChatListData)
    }

}