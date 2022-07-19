package com.app.shepherd.ui.component.carePoints.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.added_events.AddedEventModel
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.AdapterCarePointsDayBinding
import com.app.shepherd.databinding.AdapterCareTeamMembersBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso


class CarePointsDayAdapter(
    val viewModel: CreatedCarePointsViewModel,
    var carePointList: MutableList<AddedEventModel> = ArrayList()
) :
    RecyclerView.Adapter<CarePointsDayAdapter.CarePointsDayViewHolder>() {
    lateinit var binding: AdapterCarePointsDayBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openEventChat(itemData[0] as Int)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsDayViewHolder {
        context = parent.context
        binding =
            AdapterCarePointsDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsDayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return carePointList.size
        Log.e("size","ajdjq: ${carePointList.size}")
        return carePointList.size
    }

    override fun onBindViewHolder(holder: CarePointsDayViewHolder, position: Int) {
        holder.bind(position, onItemClickListener)

        setCarePointsAdapter(binding.recyclerViewEvents)
    }

    private fun setCarePointsAdapter(recyclerViewEvents: RecyclerView) {
        val carePointsEventAdapter = CarePointsEventAdapter()
        recyclerViewEvents.adapter = carePointsEventAdapter
    }
    inner class CarePointsDayViewHolder(private val itemBinding: AdapterCarePointsDayBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int, recyclerItemListener: RecyclerItemListener) {
            val carePoints = carePointList[position]
            itemBinding.data = carePoints

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    carePointList[position]
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


    fun updateCarePoints(careTeams: ArrayList<AddedEventModel>) {
        this.carePointList = careTeams
        notifyDataSetChanged()
    }

}