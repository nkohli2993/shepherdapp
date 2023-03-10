package com.shepherdapp.app.utils.extensions

/**
 * Created by Deepak Rattan on 31/05/22
 */

import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.URLSpan
import android.widget.TextView
import androidx.core.text.toSpannable


fun TextView.removeLinksUnderline() {
    val spannable = SpannableString(text)
    for (u in spannable.getSpans(0, spannable.length, URLSpan::class.java)) {
        spannable.setSpan(object : URLSpan(u.url) {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, spannable.getSpanStart(u), spannable.getSpanEnd(u), 0)
    }
    text = spannable
}


 fun TextView.stripUnderlines() {
    val s: Spannable = SpannableString(text)
    val spans = s.getSpans(
        0, s.length,
        URLSpan::class.java
    )
    for (span in spans) {
        val start = s.getSpanStart(span)
        val end = s.getSpanEnd(span)
        s.removeSpan(span)
        val span = URLSpanNoUnderline(span.url)
        s.setSpan(span, start, end, 0)
    }
    text = s
}

private class URLSpanNoUnderline(url: String?) : URLSpan(url) {
    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}

fun CharSequence.stripUnderlines() {
    if(length<=0)
        return
    val spanString = this.toSpannable()
    val spans = spanString.getSpans(
        0, length,
        URLSpan::class.java
    )
    for (span in spans) {
        val start = spanString.getSpanStart(span)
        val end = spanString.getSpanEnd(span)
        spanString.removeSpan(span)
        var span = com.shepherdapp.app.utils.URLSpanNoUnderline(span.url)
        spanString.setSpan(span, start, end, 0)
    }
}

