package com.app.shepherd.ui.component.invitations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.data.dto.invitation.Results
import com.app.shepherd.databinding.AdapterInvitationsBinding
import com.app.shepherd.view_model.InvitationViewModel
import com.squareup.picasso.Picasso


class InvitationAdapter(
    private val viewModel: InvitationViewModel,
    var results: MutableList<Results> = ArrayList()
) :
    RecyclerView.Adapter<InvitationAdapter.ContentViewHolder>() {
    lateinit var binding: AdapterInvitationsBinding

    private var onItemClickListener: OnItemClickListener? = null

    fun setClickListener(clickListener: OnItemClickListener) {
        onItemClickListener = clickListener
    }

    fun updateInvitations(results: ArrayList<Results>) {
        this.results = results
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(careTeam: CareTeam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.adapter_invitations,
            parent,
            false
        )
        return ContentViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(results[position])
    }

    inner class ContentViewHolder constructor(private var itemBinding: AdapterInvitationsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Results) {
            if (result == null) return
            itemBinding.data = result

            itemBinding.let {
                result.loveUser?.userProfiles.let { it1 ->
                    val name = it1?.firstname + " " + it1?.lastname
                    // Set full Name  of loved One
                    it.txtLovedOneName.text = name

                    // Set Profile Pic of loved One
                    Picasso.get().load(it1?.profilePhoto)
                        .placeholder(R.drawable.ic_defalut_profile_pic)
                        .into(it.imgLovedOne)
                }
            }
            /* binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                 result.isSelected = isChecked

                 onItemClickListener?.onItemClick(careTeam)
             }*/
            itemBinding.cardView.setOnClickListener { itemBinding.toggleSwitch.performClick() }
        }

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}