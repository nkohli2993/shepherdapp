package com.app.shepherd.utils.extensions

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