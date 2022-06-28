package com.app.shepherd.ui.component.addMember.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.shepherd.R
import com.app.shepherd.data.dto.care_team.CareRoles
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.databinding.AdapterAddMemberRoleBinding
import com.app.shepherd.ui.base.listeners.RecyclerItemListener
import com.app.shepherd.view_model.AddMemberViewModel

class AddMemberRoleAdapter(
    context: Context,
    var resource: Int,
    var careRoles: MutableList<CareRoles> = ArrayList()
) : ArrayAdapter<CareRoles>(context, resource, careRoles) {
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

    fun updateCareTeams(careRoles: ArrayList<CareRoles>) {
        this.careRoles = careRoles
        notifyDataSetChanged()
    }


}