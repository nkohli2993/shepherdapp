package com.shepherd.app.utils.extensions

/**
 * Created by Deepak Rattan on 31/05/22
 */
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.shepherd.app.R
import com.squareup.picasso.Picasso

@BindingAdapter("bind:getNameError")
fun getNameError(view: EditText, data: String) {
    view.onTextChanged {
        if (view.isBlank()) {
            view.error = "${view.context.getString(R.string.please_enter)} $data"
            view.requestFocus()
        } else if (view.getLength() < 3) {
            view.error = "$data ${view.context.getString(R.string.should_be_minimum_3_characters)}"
            view.requestFocus()
        }
    }
}

@BindingAdapter("bind:getEmailError")
fun getEmailError(view: EditText, data: String) {
    view.onTextChanged {
        if (view.isBlank()) {
            view.error = view.context.getString(R.string.please_enter_email_id)
            view.requestFocus()
        } else if (!view.checkString().isValidEmail()) {
            view.error = view.context.getString(R.string.please_enter_valid_email_id)
            view.requestFocus()
        }
    }
}

@BindingAdapter("bind:getPassError")
fun getPassError(view: EditText, data: String) {
    view.onTextChanged {
        if (view.isBlank()) {
            view.error = view.context.getString(R.string.please_enter_your_password)
            view.requestFocus()
        } else if (!view.isValidPassword()) {
            view.error = view.context.getString(R.string.please_enter_valid_password)
            view.requestFocus()
        }
    }
}


@BindingAdapter("bind:setPhoneError")
fun setPhoneError(view: EditText, data: String) {
    view.onTextChanged {
        if (view.isBlank()) {
            view.error = "${view.context.getString(R.string.please_enter)} $data"
            view.requestFocus()
        } else if (view.getLength() < 5) {
            view.error = "$data ${view.context.getString(R.string.should_be_minimum_5_digits)}"
            view.requestFocus()
        }
    }
}

// Load image url into ImageView Using Picasso
@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, imageUrl: String?) {
    Picasso.get().load(imageUrl)
        .placeholder(R.drawable.ic_defalut_profile_pic)
        .into(view)
}






