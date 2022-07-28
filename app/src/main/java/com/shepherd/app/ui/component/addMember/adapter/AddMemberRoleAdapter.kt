package com.shepherd.app.ui.component.addMember.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shepherd.app.R
import com.shepherd.app.data.dto.care_team.CareTeamRoles
import com.shepherd.app.databinding.AdapterAddMemberRoleBinding

class AddMemberRoleAdapter(
    context: Context,
    var resource: Int,
    var careRoles: MutableList<CareTeamRoles> = ArrayList()
) : ArrayAdapter<CareTeamRoles>(context, resource, careRoles) {
    lateinit var binding: AdapterAddMemberRoleBinding

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getDropDownItemView(position, parent)
    }

    private fun getDropDownItemView(position: Int, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.vehicle_spinner_drop_view_item, parent, false)

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val careRole = careRoles[position]
        if (careRole.name == null) {
            txtTitle.visibility = View.GONE
        }
        txtTitle.text = careRole.name

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)

        val careRole = careRoles[position]

        txtTitle.text = careRole.name

        return view
    }

    fun updateCareTeams(careRoles: ArrayList<CareTeamRoles>) {
        this.careRoles = careRoles
        notifyDataSetChanged()
    }
}