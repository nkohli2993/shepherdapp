package com.app.shepherd.ui.component.invitations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
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
        fun onItemClick(id: Int?)
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
                // Set Name  of loved One
                it.txtLovedOneName.text = result.name

                // Set Profile Pic of loved One
                Picasso.get().load(result.image)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(it.imgLovedOne)

                // Set Role of Care Team
                it.textViewCareTeamRole.text = "As " + result.careRoles?.name

                // Set toggle
                binding.toggleSwitch.isChecked = result.isSelected

            }
            binding.toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
                result.isSelected = isChecked

                if (isChecked) {
                    onItemClickListener?.onItemClick(result.id)
                }
            }
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