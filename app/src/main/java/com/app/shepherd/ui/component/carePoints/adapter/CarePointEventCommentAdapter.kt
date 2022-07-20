package com.app.shepherd.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.data.dto.added_events.EventCommentsModel
import com.app.shepherd.databinding.AdapterChatBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener

class CarePointEventCommentAdapter(
    var commentList: ArrayList<EventCommentsModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointEventCommentAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterChatBinding
    lateinit var context: Context

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {

        }
    }

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
        return when {
            commentList.size <= 3 -> {
                commentList.size
            }
            else -> {
                3
            }
        }

    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position, onItemClickListener)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
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

}