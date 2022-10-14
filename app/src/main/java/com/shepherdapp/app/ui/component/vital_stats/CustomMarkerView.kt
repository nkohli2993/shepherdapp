package com.shepherdapp.app.ui.component.vital_stats

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.shepherdapp.app.R


/**
 * Created by Deepak Rattan on 14/10/22
 */
class CustomMarkerView(context: Context?, layoutResource: Int, valueSize: Int, valueColor: Int) :
    MarkerView(context, layoutResource) {
    private var tvContent: TextView


    override fun refreshContent(e: Entry, highlight: Highlight?) {
        // set the entry-value as the display text
        tvContent.text = "x: ${e.x} y: ${e.y}"

        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // center the marker horizontally and vertically
            mOffset = MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
        return super.getOffset()
    }

    init {
        // this markerView only displays a textview
        val layout = LayoutInflater.from(context).inflate(layoutResource, null) as LinearLayout
//        tvContent = findViewById(R.id.tvContent)
        tvContent = layout.findViewById(R.id.tvContent)
        tvContent.textSize = valueSize.toFloat()
        tvContent.setTextColor(valueColor)
    }
}