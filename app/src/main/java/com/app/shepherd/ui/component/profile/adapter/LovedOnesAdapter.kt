package com.app.shepherd.ui.component.profile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.databinding.AdapterLovedOnesBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.ProfileViewModel
import com.squareup.picasso.Picasso


class LovedOnesAdapter(
    private val viewModel: ProfileViewModel,
    var lovedOneProfileList: MutableList<UserProfiles> = ArrayList()
) :
    RecyclerView.Adapter<LovedOnesAdapter.LovedOnesViewHolder>() {
    lateinit var binding: AdapterLovedOnesBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
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
        return lovedOneProfileList.size
    }

    override fun onBindViewHolder(holder: LovedOnesViewHolder, position: Int) {
        holder.bind(lovedOneProfileList[position], onItemClickListener)
    }


    class LovedOnesViewHolder(private val itemBinding: AdapterLovedOnesBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(userProfiles: UserProfiles, recyclerItemListener: RecyclerItemListener) {
            // itemBinding.data = dashboard
            val fullName = userProfiles.firstname + " " + userProfiles.lastname
            itemBinding.txtLovedOneName.text = fullName

            Picasso.get().load(userProfiles.profilePhoto).placeholder(R.drawable.ic_defalut_profile_pic)
                .into(itemBinding.imgLovedOne)

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    userProfiles
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

    fun addData(lovedOneProfileList: ArrayList<UserProfiles>?) {
        this.lovedOneProfileList.clear()
        lovedOneProfileList?.let { this.lovedOneProfileList.addAll(it) }
        notifyDataSetChanged()
    }

}