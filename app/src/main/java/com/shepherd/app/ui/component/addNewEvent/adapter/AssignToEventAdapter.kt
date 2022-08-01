package com.shepherd.app.ui.component.addNewEvent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.view_model.AddNewEventViewModel
import com.squareup.picasso.Picasso


class AssignToEventAdapter(
    val onListener: selectedTeamMember,
    val context: Context,
    private val viewModel: AddNewEventViewModel,
    var memberList: ArrayList<CareTeamModel> = ArrayList()
) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return memberList.size
    }

    override fun getItem(position: Int): Any {
        return memberList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.adapter_assign_to_event, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        if (memberList[position].id == -1) {
            vh.tvSelect.isVisible = true
            vh.clEventWrapper.isVisible = false
        } else {
            vh.tvSelect.isVisible = false
            vh.clEventWrapper.isVisible = true
            vh.textViewCareTeamName.text = memberList[position].user_id_details.firstname.plus(" ")
                .plus(memberList[position].user_id_details.lastname)
            vh.textViewCareTeamRole.text = memberList[position].careRoles.name
            Picasso.get().load(memberList[position].user_id_details.profilePhoto)
                .placeholder(R.drawable.ic_defalut_profile_pic)
                .into(vh.imageViewCareTeam)
        }

        vh.checkbox.setOnCheckedChangeListener { compoundButton, b ->
            onListener.onSelected(position)
        }
        vh.clEventWrapper.setOnClickListener {
            vh.checkbox.isChecked = !vh.checkbox.isChecked
            onListener.onSelected(position)
        }
        return view
    }

    private class ItemHolder(row: View?) {
        val textViewCareTeamName: TextView
        val textViewCareTeamRole: TextView
        val tvSelect: TextView
        val checkbox: CheckBox
        val imageViewCareTeam: ImageView
        val clEventWrapper: ConstraintLayout
        init {
            textViewCareTeamName = row?.findViewById(R.id.textViewCareTeamName) as TextView
            textViewCareTeamRole = row.findViewById(R.id.textViewCareTeamRole) as TextView
            tvSelect = row.findViewById(R.id.tvSelect) as TextView
            checkbox = row.findViewById(R.id.checkbox) as CheckBox
            imageViewCareTeam = row.findViewById(R.id.imageViewCareTeam) as ImageView
            clEventWrapper = row.findViewById(R.id.clEventWrapper) as ConstraintLayout
        }
    }

    interface selectedTeamMember {
        fun onSelected(position: Int)
    }
}