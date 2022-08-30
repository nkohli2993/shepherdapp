package com.shepherd.app.ui.component.resources

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup


class FlowLayout : ViewGroup {
    private var paddingHorizontal = 30
    private var paddingVertical = 30

    constructor(context: Context?) : super(context) {
        init()
    }

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int = 0) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
//        paddingHorizontal = resources.getDimensionPixelSize(R.dimen.flowlayout_horizontal_padding)
//        paddingVertical = resources.getDimensionPixelSize(R.dimen.flowlayout_vertical_padding)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var childLeft = paddingLeft
        var childTop = paddingTop
        var lineHeight = 0
        // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
        val myWidth = resolveSize(100, widthMeasureSpec)
        var wantedHeight = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            // let the child measure itself
            child.measure(
                getChildMeasureSpec(widthMeasureSpec, 0, child.layoutParams.width),
                getChildMeasureSpec(heightMeasureSpec, 0, child.layoutParams.height)
            )
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            // lineheight is the height of current line, should be the height of the heightest view
            lineHeight = Math.max(childHeight, lineHeight)
            if (childWidth + childLeft + paddingRight > myWidth) {
                // wrap this line
                childLeft = paddingLeft
                childTop += paddingVertical + lineHeight
                lineHeight = childHeight
            }
            childLeft += childWidth + paddingHorizontal
        }
        wantedHeight += childTop + lineHeight + paddingBottom
        setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var childLeft = paddingLeft
        var childTop = paddingTop
        var lineHeight = 0
        val myWidth = right - left
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            lineHeight = Math.max(childHeight, lineHeight)
            if (childWidth + childLeft + paddingRight > myWidth) {
                childLeft = paddingLeft
                childTop += paddingVertical + lineHeight
                lineHeight = childHeight
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
            childLeft += childWidth + paddingHorizontal
        }
    }
}