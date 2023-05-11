package com.shepherdapp.app.utils

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.shepherdapp.app.BuildConfig

fun Context.toast(message: () -> String) {
    Toast.makeText(this, message(), Toast.LENGTH_LONG).show()
}

fun Context.log(message:String,log_text:String) {
    if(BuildConfig.DEBUG){
        Log.e(log_text,message)
    }
}

fun Context.colorList(id: Int): ColorStateList {
    return ColorStateList.valueOf(ContextCompat.getColor(this, id))
}

fun Context.showKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
}

fun Context.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.toggleKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    if (imm.isActive) {
        hideKeyboard()
    } else {
        showKeyboard()
    }
}