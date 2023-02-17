package com.shepherdapp.app.ui.component.carePoints.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.UserAssigneeModel
import com.shepherdapp.app.databinding.AdapterEventsMembersBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.TextDrawable
import com.shepherdapp.app.utils.setImageFromUrl
import com.squareup.picasso.Picasso


class CarePointsListEventAdapter(
    var commentList: ArrayList<UserAssigneeModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsListEventAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterEventsMembersBinding
    lateinit var context: Context
    private var TAG = "CarePointsListEventAdapter"

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterEventsMembersBinding.inflate(
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


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterEventsMembersBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            // Set the margin end as zero for the last item
            if (position == commentList.size - 1) {
                itemBinding.layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.marginEnd = 0
                }
            }

            val imageUrl = commentList[position].user_details.profilePhoto ?: ""
            val firstName = commentList[position].user_details.firstname
            val lastName = commentList[position].user_details.lastname


            Log.d(TAG, "FirstName : $firstName")
            Log.d(TAG, "lastName : $lastName")

            itemBinding.let {
                it.imageView.setImageFromUrl(
                    imageUrl,
                    commentList[position].user_details.firstname,
                    commentList[position].user_details.lastname
                )
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