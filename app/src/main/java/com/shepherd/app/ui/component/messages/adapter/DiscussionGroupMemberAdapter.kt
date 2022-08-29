package com.shepherd.app.ui.component.messages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.chat.ChatUserDetail
import com.shepherd.app.databinding.AdapterDiscussionGroupsMembersBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.view_model.MessagesViewModel
import com.squareup.picasso.Picasso


class DiscussionGroupMemberAdapter(
    private val viewModel: MessagesViewModel,
    var requestList: MutableList<ChatUserDetail?> = ArrayList()
) :
    RecyclerView.Adapter<DiscussionGroupMemberAdapter.DiscussionGroupMemberViewHolder>() {
    lateinit var binding: AdapterDiscussionGroupsMembersBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DiscussionGroupMemberViewHolder {
        context = parent.context
        binding =
            AdapterDiscussionGroupsMembersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DiscussionGroupMemberViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: DiscussionGroupMemberViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class DiscussionGroupMemberViewHolder(private val itemBinding: AdapterDiscussionGroupsMembersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(chatListData: ChatUserDetail?, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            if (!chatListData?.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(chatListData?.imageUrl)
                    .placeholder(R.drawable.ic_defalut_profile_pic).into(itemBinding.ivMember)
            }

            itemBinding.root.setOnClickListener {
                chatListData?.let { it1 ->
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(chatListData: ArrayList<ChatUserDetail?>) {
        this.requestList.clear()
        this.requestList.addAll(chatListData)
        notifyDataSetChanged()
    }

}