package com.app.shepherd.utils.extensions

/**
 * Created by Deepak Rattan on 31/05/22
 */
import android.annotation.SuppressLint
import android.app.Service
import android.graphics.Typeface
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.BufferType
import androidx.core.content.ContextCompat
import com.app.shepherd.R
import com.app.shepherd.utils.Drawable.END
import com.app.shepherd.utils.Drawable.START
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}


fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged.invoke(s.toString())
        }
    })
}


fun EditText.isBlank(): Boolean {
    return this.text.toString().trim().isEmpty()
}


fun EditText.getLength(): Int {
    return this.text.toString().trim().length
}

fun EditText.checkString(): String {
    return this.text.toString().trim()
}

fun TextView.isBlank(): Boolean {
    return this.text.toString().trim().isEmpty()
}


fun TextView.getLength(): Int {
    return this.text.toString().trim().length
}

fun TextView.checkString(): String {
    return this.text.toString().trim()
}


fun TextInputEditText.setStrokeColor(color: Int) {
    this.setHintTextColor(color)
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onDrawableClick(drawableType: Int, onClick: () -> Unit) {
    this.setOnTouchListener(View.OnTouchListener { v, event ->

        if (event.action == MotionEvent.ACTION_UP) {
            when (drawableType) {
                END -> {
                    if (event.rawX >= this.right - (this.compoundDrawables.get(
                            END
                        ).bounds.width() + this.paddingRight)
                    ) { // your action here
                        onClick()
                        return@OnTouchListener true
                    }
                }
                START -> {
                    if (event.rawX <= (this.compoundDrawables[START].bounds.width() + this.paddingStart)) {
                        onClick()

                        return@OnTouchListener true
                    }
                }
            }
        }
        false
    })
}

@SuppressLint("ClickableViewAccessibility")
fun TextView.onDrawableClick(drawableType: Int, onClick: () -> Unit) {
    this.setOnTouchListener(View.OnTouchListener { v, event ->

        if (event.action == MotionEvent.ACTION_DOWN) {
            when (drawableType) {
                END -> {
                    if (event.rawX >= this.right - (this.compoundDrawables.get(
                            END
                        ).bounds.width() + this.paddingRight)
                    ) { // your action here
                        onClick()
                        return@OnTouchListener true
                    }
                }
                START -> {
                    if (event.rawX <= (this.compoundDrawables[START].bounds.width() + this.paddingStart)) {
                        onClick()

                        return@OnTouchListener true
                    }
                }
            }
        }
        false
    })
}


fun TextView.setSpanString(
    spanText: String, start: Int, end: Int = spanText.length,
    showBold: Boolean = false, color: Int = R.color.colorPrimary, onSpanClick: () -> Unit = {}
) {
    val ss = SpannableString(spanText)
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    text = ss
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)
}

fun TextView.setSpanString(
    spanText: String, start: Int, end: Int, start2: Int, end2: Int,
    showBold: Boolean = false, color: Int = R.color.colorPrimary,
    onSpanClick: (value: Int) -> Unit = {}
) {
    val ss = SpannableString(spanText)

    val termsAndCondition: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(1)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    val privacy: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            onSpanClick(2)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
            ds.color = ContextCompat.getColor(this@setSpanString.context, color)
        }
    }

    ss.setSpan(termsAndCondition, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    ss.setSpan(privacy, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    if (showBold) {
        ss.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    this.setText(ss, BufferType.SPANNABLE)
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = ContextCompat.getColor(this@setSpanString.context, R.color.transparent)
}


fun String.isValidEmail(): Boolean =
    this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()


fun EditText.isValidPassword(): Boolean {
    return Pattern
        .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!&^%$#@()=*/.+_-])(?=\\S+$).{8,}$")
        .matcher(this.checkString()).matches()
}

fun String.isValidPassword(): Boolean {
    return Pattern
        .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!&^%$#@()=*/.+_-])(?=\\S+$).{8,}$")
        .matcher(this.checkString()).matches()
}

fun EditText.isHashTag(): Boolean {
    return Pattern.compile("[#]+[A-Za-z0-9-_]+\\b")
        .matcher(this.checkString()).matches()
}

fun String.isHashTag(): Boolean {
    return Pattern.compile("[#]+[A-Za-z0-9-_]+\\b")
        .matcher(this.checkString()).matches()
}

fun EditText.isValidName(): Boolean {
    return Pattern
        .compile("^(?=.*[A-Za-z])(?=\\S+\$).{3,}$")
        .matcher(checkString()).matches()
}

fun String.isValidName(): Boolean {
    return Pattern
        .compile("^(?=.*[A-Za-z])(?=\\S+\$).{3,}$")
        .matcher(checkString()).matches()
}

fun String.isBlank(): Boolean {
    return this.trim().isEmpty()
}


fun String.getLength(): Int {
    return this.trim().length
}

fun String.checkString(): String {
    return this.trim()
}


fun EditText.setFocus() {

    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
}

fun EditText.showSoftKeyboard() {
    val imm = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}

fun EditText.hideSoftKeyboard() {

    val im = context.getSystemService(Service.INPUT_METHOD_SERVICE) as InputMethodManager
    im.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.removeSpecialChar(s: String): String {
    return this.text.toString().trim().replace(s, "")
}

