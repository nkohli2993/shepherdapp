package com.shepherd.app.ui.component.lockBox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypes

class DocumentAdapter (
    context: Context,
    var resource: Int,
    var relations: ArrayList<LockBoxTypes>
) : ArrayAdapter<LockBoxTypes>(context, resource, relations) {

    private var mContext: Context? = null

    init {
        mContext = context
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getDropDownItemView(position, parent)
    }

    private fun getDropDownItemView(position: Int, parent: ViewGroup): View {
        val view: View = LayoutInflater.from(mContext)
            .inflate(R.layout.spinner_drop_down_list, parent, false)

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val relation = relations[position]
        if (relation.name == null) {
            txtTitle.visibility = View.GONE
        }
        txtTitle.text = relation.name

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, parent)
    }

    private fun createItemView(position: Int, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resource, parent, false)

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)

        val relation = relations[position]

        txtTitle.text = relation.name

        return view
    }
}