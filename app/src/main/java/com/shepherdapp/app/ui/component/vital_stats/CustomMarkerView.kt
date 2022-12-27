package com.shepherdapp.app.ui.component.vital_stats

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.shepherdapp.app.R
import com.shepherdapp.app.utils.Const
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject


/**
 * Created by Deepak Rattan on 14/10/22
 */
@SuppressLint("ViewConstructor")
class CustomMarkerView @Inject constructor(
    context: Context?,
    layoutResource: Int,
    private val type: String?
) :
    MarkerView(context, layoutResource) {

    var view: View? = null
    var txtContent: TextView? = null


    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry, highlight: Highlight?) {

        // set the entry-value as the display text
//        txtContent?.text = "y: ${e.y}"
        var text: String? = null

        when (type) {
            Const.VitalStat.BLOOD_PRESSURE -> {
                text = if (highlight?.dataSetIndex == 0)
                    "${e.y} dbp"
                else
                    "${e.y} sbp"
            }
            Const.VitalStat.OXYGEN -> {
                text = "${e.y} SpO2"
            }
            Const.VitalStat.HEART_RATE -> {
                text = "${e.y} bpm"
            }
            Const.VitalStat.BODY_TEMP -> {
                // Round Off to two decimal places
                val df = DecimalFormat("#.##")
                df.roundingMode = RoundingMode.DOWN
                val roundOff = df.format(e.y)
                text = "$roundOff F"
            }
        }
        txtContent?.text = text
        Log.d(TAG, "refreshContent: x :${e.x} and y is ${e.y}")
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

    override fun getOffsetForDrawingAtPoint(posX: Float, posY: Float): MPPointF {
        return MPPointF(-width / 2f, -height - 10f)
    }

    init {
        txtContent = findViewById(R.id.txtContent)
    }
}