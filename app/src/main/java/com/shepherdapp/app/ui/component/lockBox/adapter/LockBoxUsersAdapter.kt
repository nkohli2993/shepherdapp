package com.shepherdapp.app.ui.component.lockBox.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterUsersLockboxBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.AddNewLockBoxViewModel


class LockBoxUsersAdapter(
    var usersList: ArrayList<CareTeamModel> = ArrayList(),
    val addNewLockBoxViewModel: AddNewLockBoxViewModel
) :
    RecyclerView.Adapter<LockBoxUsersAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterUsersLockboxBinding
    lateinit var context: Context
    private var TAG = "CarePointsListEventAdapter"

    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            addNewLockBoxViewModel.openUserListMedication("show")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterUsersLockboxBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return when {
            usersList.size <= 5 -> {
                usersList.size
            }
            else -> {
                5
            }
        }

    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position, onItemClickListener)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterUsersLockboxBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            // Set the margin end as zero for the last item
            if (position == usersList.size - 1) {
                itemBinding.layout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    this.marginEnd = 0
                }
            }

            val imageUrl = usersList[position].user_id_details?.profilePhoto ?: ""
            val firstName = usersList[position].user_id_details?.firstname
            val lastName = usersList[position].user_id_details?.lastname


            Log.d(TAG, "FirstName : $firstName")
            Log.d(TAG, "lastName : $lastName")

            itemBinding.let {
                itemBinding.imageView.setImageFromUrl(imageUrl, firstName, lastName)

            }
            itemBinding.root.setOnClickListener {
                onItemClickListener.onItemSelected()
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(usersList: ArrayList<CareTeamModel>) {
        this.usersList = usersList
        notifyDataSetChanged()
    }

}