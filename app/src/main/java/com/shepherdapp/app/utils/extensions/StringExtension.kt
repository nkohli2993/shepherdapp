package com.shepherdapp.app.utils.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Deepak Rattan on 13/07/22
 */
// Insert hyphen after every 3 characters in the string
fun String.getStringWithHyphen(str: String): String {
    var resultant = ""
    resultant = if (str.length <= 3) str else "" + str[0] + str[1] + str[1]
    for (i in 3 until str.length) {
        if (i % 3 == 0) {
            resultant += "-" + str[i]
        } else {
            resultant += str[i]
        }
    }
    return resultant
}

// Convert Timestamp String into date string
fun String.toTextFormat(
    input: String,
    inputFormatStr: String?,
    outputFormatStr: String?
): String {

    var inputString = input
    var inputFormatString = inputFormatStr
    var outputFormatString = outputFormatStr
    if (inputFormatString == null) {
        inputFormatString = "yyyy-MM-dd HH:mm:ss"
    }
    if (outputFormatString == null) {
        outputFormatString = inputFormatStr
    }
    val inputDateFormat = SimpleDateFormat(inputFormatString, Locale.getDefault())
    inputDateFormat.timeZone = TimeZone.getDefault()
    try {
        val date = inputDateFormat.parse(inputString)
        if (date != null) {
            val outputDateFormat = SimpleDateFormat(outputFormatString, Locale.getDefault())
            outputDateFormat.timeZone = TimeZone.getDefault()
            return outputDateFormat.format(date)
        }
    } catch (ignored: ParseException) {
        ignored.printStackTrace()
    }
    return "NA"
}