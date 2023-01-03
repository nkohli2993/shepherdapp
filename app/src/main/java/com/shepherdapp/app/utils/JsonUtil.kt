package com.shepherdapp.app.utils

import com.google.gson.Gson

/**
 * Created by Deepak Rattan on 03/01/23
 */

// Extension function to convert JSON string to model class
inline fun <reified T : Any> String.toKotlinObject(): T = Gson().fromJson(this, T::class.java)