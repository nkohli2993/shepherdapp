package com.shepherdapp.app.utils

import android.content.Context
import com.shepherdapp.app.R
import java.util.regex.Pattern


/**
 * Created by Sumit Kumar on 14/10/2017.
 */

object RegexUtils {
    private val EMAIL_ADDRESS: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private val PASSWORD: Pattern = Pattern.compile(
        "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$"
    )

    fun isValidEmail(email: String): Boolean {
        return EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return PASSWORD.matcher(password).matches()
    }

    fun passwordValidated(context:Context, password: String): Boolean {
        var errorMessage = ""
        return when {
            password.length < 8 -> {
                errorMessage = context.getString(R.string.password_at_least_8_characters)
                context.toast { errorMessage }
                false
            }
            !password.matches(".*[!@#$%^&*+=/?].*".toRegex()) -> {
                errorMessage = "Password must contain 1 symbol"
                false
            }
            !password.matches("(?=.*[0-9])".toRegex()) -> {
                errorMessage = "Password must contain 1 digit"
                false
            }
            !password.matches("(?=.*[@#\$%^&+=])".toRegex()) -> {
                errorMessage = "Password must contain 1 special character"
                false
            }
            else -> true
        }

    }
}
