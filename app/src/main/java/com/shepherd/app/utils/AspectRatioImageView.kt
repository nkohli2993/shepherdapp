package com.shepherd.app.utils

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class AspectRatioImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!) {
        this.scaleType = ScaleType.FIT_XY
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        this.scaleType = ScaleType.FIT_XY
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    ) {
        this.scaleType = ScaleType.FIT_XY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = drawable
        if (d != null && d.intrinsicWidth > 0) {
            var width = MeasureSpec.getSize(widthMeasureSpec)
            if (width <= 0) width = layoutParams.width
            val height = width * d.intrinsicHeight / d.intrinsicWidth
            setMeasuredDimension(width, height)
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}