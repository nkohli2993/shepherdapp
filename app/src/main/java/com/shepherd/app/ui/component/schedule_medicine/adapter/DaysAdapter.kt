package com.shepherd.app.ui.component.schedule_medicine.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.isVisible
import com.shepherd.app.R


class DaysAdapter(context: Context, val alFrequency: Array<String>) :
    BaseAdapter() {
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return alFrequency.size
    }

    override fun getItem(position: Int): Any {
        return alFrequency[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_add_medicine_list, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
        vh.cbDay.isVisible = position != 0
        vh.cbDay.text = alFrequency.get(position)

        return view
    }

    private class ItemHolder(row: View?) {
        val cbDay: CheckBox
        init {
            cbDay = row!!.findViewById(R.id.cbDay) as CheckBox
        }
    }
}