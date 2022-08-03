package com.shepherd.app.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.added_events.UserAssigneeModel
import com.shepherd.app.databinding.AdapterAssigneeMenrbersBinding
import com.shepherd.app.databinding.AdapterEventsMembersBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.squareup.picasso.Picasso


class CarePointsEventAdapter(
    var commentList: ArrayList<UserAssigneeModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsEventAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterAssigneeMenrbersBinding
    lateinit var context: Context

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {

        }
    }

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


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterAssigneeMenrbersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            // Set the margin end as zero for the last item
            if (position == commentList.size - 1) {
                itemBinding.layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.marginEnd = 0
                }
            }

            val imageUrl = commentList[position].user_details.profilePhoto ?: ""
            itemBinding.let {
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get().load(imageUrl).placeholder(R.drawable.default_ic)
                        .into(it.imageView)
                } else {
                    it.imageView.setImageResource(R.drawable.default_ic)
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

}