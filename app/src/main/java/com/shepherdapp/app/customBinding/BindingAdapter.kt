package com.shepherdapp.app.customBinding

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.shepherdapp.app.R
import com.shepherdapp.app.utils.TextDrawable
import com.squareup.picasso.Picasso

/*
  Created by "Sumit Kumar"
*/

@BindingAdapter("setError")
fun setError(textInputLayout: TextInputLayout, error: String?) {
    textInputLayout.error = error
}

@BindingAdapter("setError")
fun setErrorEditText(editText: EditText, error: String?) {
    editText.error = error
}

@BindingAdapter("setVisibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}


@BindingAdapter("app:tint")
fun ImageView.setImageTint(@ColorInt color: Int) {
    setColorFilter(color)
}




