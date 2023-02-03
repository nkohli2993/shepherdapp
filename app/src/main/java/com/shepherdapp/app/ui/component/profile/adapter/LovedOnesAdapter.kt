package com.shepherdapp.app.ui.component.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.databinding.AdapterLovedOnesBinding
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.view_model.ProfileViewModel
import com.squareup.picasso.Picasso


class LovedOnesAdapter(
    private val viewModel: ProfileViewModel,
    var careTeams: MutableList<CareTeamModel> = ArrayList()
) :
    RecyclerView.Adapter<LovedOnesAdapter.LovedOnesViewHolder>() {
    lateinit var binding: AdapterLovedOnesBinding
    lateinit var context: Context
    private var onItemClickListener: OnItemClickListener? = null
    private var lastSelectedPosition = -1
    private var dismissLastSelectedOption: (() -> Unit)? = null

    interface OnItemClickListener {
        fun onItemClick(careTeam: CareTeamModel)
    }


    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LovedOnesViewHolder {
        context = parent.context
        binding =
            AdapterLovedOnesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LovedOnesViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return careTeams.size
    }

    override fun onBindViewHolder(holder: LovedOnesViewHolder, position: Int) {
        holder.bind(careTeams[position])
    }


    inner class LovedOnesViewHolder(private val itemBinding: AdapterLovedOnesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(careTeam: CareTeamModel) {
            // itemBinding.data = dashboard
            careTeam.let {
                // Set full name
                val fullName =
                    it.love_user_id_details?.firstname + " " + if (it.love_user_id_details?.lastname != null) it.love_user_id_details?.lastname else ""
                itemBinding.txtLovedOneName.text = fullName

                //Set Image
                Picasso.get().load(it.love_user_id_details?.profilePhoto)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(itemBinding.imgLovedOne)

                // Set Role
                itemBinding.textLovedOneRole.text = it.careRoles?.name
                itemBinding.textLovedOneRole.visibility  = View.GONE
                // Get lovedOneID from Shared Pref
                val lovedOneIDInPrefs =
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
                itemBinding.checkbox.isChecked =
                    lovedOneIDInPrefs.equals(it.love_user_id_details?.uid)

                if (lovedOneIDInPrefs.equals(it.love_user_id_details?.uid)) {
                    dismissLastSelectedCareTeam(itemBinding, bindingAdapterPosition)
                    lastSelectedPosition = bindingAdapterPosition
                    itemBinding.checkbox.isChecked = true
                    careTeam.isSelected = true
                } else {
                    itemBinding.checkbox.isChecked = false
                    careTeam.isSelected = false
                }
            }

            itemBinding.checkbox.setOnClickListener {
                deselectCareTeams()
                careTeam.isSelected = true
                dismissLastSelectedCareTeam(itemBinding, bindingAdapterPosition)
                lastSelectedPosition = bindingAdapterPosition
                itemBinding.checkbox.isChecked = true
                onItemClickListener?.onItemClick(careTeam)
            }
        }
    }

    private fun deselectCareTeams() {
        careTeams.forEach {
            it.isSelected = false
        }
    }

    private fun dismissLastSelectedCareTeam(binding: AdapterLovedOnesBinding, position: Int) {
        if (lastSelectedPosition != -1) {
            dismissLastSelectedOption?.invoke()
            lastSelectedPosition = position
        }
        dismissLastSelectedOption = listener@{
            binding.checkbox.isChecked = false
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(careTeams: ArrayList<CareTeamModel>?) {
        careTeams?.let { this.careTeams.addAll(it) }
        notifyDataSetChanged()
    }

}