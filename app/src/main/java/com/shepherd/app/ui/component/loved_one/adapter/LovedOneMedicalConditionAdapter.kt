package com.shepherd.app.ui.component.loved_one.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.Payload
import com.shepherd.app.databinding.AdapterMedicalConditionsBinding
import com.shepherd.app.view_model.LovedOneMedicalConditionViewModel

class LovedOneMedicalConditionAdapter(
    private val viewModel: LovedOneMedicalConditionViewModel,
    var payloads: MutableList<Payload> = ArrayList()
) :
    RecyclerView.Adapter<LovedOneMedicalConditionAdapter.ContentViewHolder>() {
    lateinit var binding: AdapterMedicalConditionsBinding
    lateinit var context: Context


    interface OnItemClickListener {
        fun onItemClick(careTeam: CareTeamModel, type: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        context = parent.context
        binding = AdapterMedicalConditionsBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return payloads.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(payloads[position])
    }

    inner class ContentViewHolder constructor(private var itemBinding: AdapterMedicalConditionsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(payload: Payload) {
            /* if (payload.conditions?.name.isNullOrEmpty()) {
                 itemBinding.txtNoMedicalConditions.visibility = View.VISIBLE
             } else {
                 itemBinding.txtMedicalCondition.text = payload.conditions?.name
             }*/

            if (!payload.conditions?.name.isNullOrEmpty()) {
                itemBinding.txtDot.visibility = View.VISIBLE
                itemBinding.txtMedicalCondition.text = payload.conditions?.name
            } else {
                itemBinding.txtDot.visibility = View.GONE
            }
        }

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(payloads: ArrayList<Payload>?) {
        // this.careTeams.clear()
        payloads?.let { this.payloads.addAll(it) }
        notifyDataSetChanged()
    }

}