package com.app.shepherd.ui.component.loved_one

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.databinding.RvLoveOnesBinding
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import com.app.shepherd.view_model.LovedOneViewModel
import com.squareup.picasso.Picasso

class LovedOneAdapter(
    private val viewModel: LovedOneViewModel,
    var careTeams: MutableList<CareTeam> = ArrayList()
) :
    RecyclerView.Adapter<LovedOneAdapter.ContentViewHolder>() {
    lateinit var binding: RvLoveOnesBinding

    private var onItemClickListener: OnItemClickListener? = null

    private var lastSelectedPosition = -1
    private var dismissLastSelectedOption: (() -> Unit)? = null

    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(careTeam: CareTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.rv_love_ones,
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return careTeams.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(careTeams[position])
    }

    inner class ContentViewHolder constructor(private var itemBinding: RvLoveOnesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(careTeam: CareTeam) {
            if (careTeam == null) return
            careTeam.let {
                // Set full name
                val fullName = it.loveUser?.firstname + " " + it.loveUser?.lastname
                itemBinding.textViewCareTeamName.text = fullName

                //Set Image
                Picasso.get().load(it.loveUser?.profilePhoto)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(itemBinding.imageViewCareTeam)

                // Set Role
                itemBinding.txtRole.text = "As " + it.careRoles?.name

                // Get lovedOneID from Shared Pref
                val lovedOneIDInPrefs =
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
                itemBinding.checkbox.isChecked = lovedOneIDInPrefs.equals(it.loveUserId)

                if (lovedOneIDInPrefs.equals(it.loveUserId)) {
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

    private fun dismissLastSelectedCareTeam(binding: RvLoveOnesBinding, position: Int) {
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

    fun addData(careTeams: ArrayList<CareTeam>?) {
        this.careTeams.clear()
        careTeams?.let { this.careTeams.addAll(it) }
        notifyDataSetChanged()
    }

}