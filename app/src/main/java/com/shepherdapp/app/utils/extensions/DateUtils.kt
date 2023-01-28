package com.shepherdapp.app.utils.extensions

import android.annotation.SuppressLint
import com.google.android.gms.fitness.data.DataPoint
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Deepak Rattan on 18/08/22
 */


fun String?.changeDateFormat(
    sourceDateFormat: String = "yyyy-MM-dd HH:mm:ss",
    targetDateFormat: String = "dd MMM, yyyy hh:mm a"
): String {
    if (this.isNullOrEmpty()) {
        return ""
    }
    val date = createDate(sourceDateFormat)
    return date.getStringDate(targetDateFormat)
}


fun Date?.getStringDate(format: String? = "yyyy-MM-dd"): String {
    if (this == null || format == null || format.isEmpty()) {
        return ""
    }
    val outputDateFormat = SimpleDateFormat(format, Locale.getDefault())
    return outputDateFormat.format(this)
}


fun String?.createDate(sourceFormat: String = "yyyy-MM-dd", isUTCToLocal: Boolean = false): Date {
    if (this.isNullOrEmpty()) {
        return Date()
    }
    val inputDateFromat = SimpleDateFormat(sourceFormat, Locale.getDefault())

    var date = Date()
    if (isUTCToLocal) {
        inputDateFromat.timeZone = TimeZone.getTimeZone("UTC")
        date = inputDateFromat.parse(this)
        inputDateFromat.timeZone = TimeZone.getDefault()
        val formattedDate: String = inputDateFromat.format(date)

        date = inputDateFromat.parse(formattedDate)
    } else {
        try {
            date = inputDateFromat.parse(this)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


    return date
}


fun Long?.createDate(): Date {
    val inputDateFromat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var date = Date()
    if (this == null) {
        try {
            date = inputDateFromat.parse(Date(this).toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    return date
}


fun Date?.getTimeAgo(): String {
    var getDate = ""
    try {

        val now = Date()
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(now.time - this!!.time)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(now.time - this.time)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(now.time - this.time)
        val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - this.time)


        getDate = when {
            seconds <= 0 -> {
                "Just Now"
            }
            seconds < 60 -> {
                "$seconds seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hours ago"
            }
            days >= 7 -> {
                if (days > 360) {
                    "${(days / 360)} ${
                        if ((days / 360) > 1) {
                            "Years"
                        } else {
                            "Year"
                        }
                    } ago"
                } else if (days > 30) {
                    "${(days / 30)} ${
                        if ((days / 30) > 1) {
                            "Months"
                        } else {
                            "Month"
                        }
                    } ago"
                } else {
                    "${((days / 7))} ${
                        if (((days / 7)) > 1) {
                            "Weeks"
                        } else {
                            "Week"
                        }
                    } ago"
                }

            }
            else -> {
                "$days ${
                    if (days > 1) {
                        "days"
                    } else {
                        "day"
                    }
                } ago"
            }


        }


    } catch (j: Exception) {
        j.printStackTrace()
    }

    return getDate
}

fun Date?.getNotificationTimeAgo(): String {
    var getDate = ""
    try {

        val now = Date()
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(now.time - this!!.time)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(now.time - this.time)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(now.time - this.time)
        val days: Long = TimeUnit.MILLISECONDS.toDays(now.time - this.time)


        getDate = when {
            seconds < 60 -> {
                "Today"
            }
            minutes < 60 -> {
                "Today"
            }
            hours < 24 -> {
                "Today"
            }

            days >= 7 -> {
                if (days > 360) {
                    if ((days / 360) > 1) {
                        "${(days / 360)} Years ago"
                    } else {
                        "Last Year"
                    }

                } else if (days > 30) {
                    if ((days / 30) > 1) {
                        "${(days / 30)} Months ago"
                    } else {
                        "Last Month"
                    }
                } else {
                    if (((days / 7)) > 1) {
                        "${((days / 7))} Weeks ago"
                    } else {
                        "Last Week"
                    }
                }

            }
            else -> {

                if (days > 1) {
                    "This week"
                } else {
                    "Yesterday"
                }

            }


        }


    } catch (j: Exception) {
        j.printStackTrace()
    }

    return getDate
}


fun String?.getChatDate(
    sourceDateFormat: String? = "yyyy-MM-dd HH:mm:ss",
    targetDateFormat: String? = "dd MMM, yyyy"
): String {
    val date = createDate(sourceDateFormat ?: "yyyy-MM-dd HH:mm:ss")
    val cal = Calendar.getInstance()
    val selectedData = Calendar.getInstance().apply {
        time = date
    }
    if (cal.get(Calendar.DATE) == selectedData.get(Calendar.DATE) && cal.get(Calendar.YEAR) == selectedData.get(
            Calendar.YEAR
        ) && cal.get(Calendar.MONTH) == selectedData.get(Calendar.MONTH)
    ) {
        return "Today"
    } else {
        return changeDateFormat(
            sourceDateFormat ?: "yyyy-MM-dd HH:mm:ss",
            targetDateFormat ?: "dd MMM, yyyy"
        )
    }
}

// Convert String timestamp into string of date
fun String?.convertISOTimeToDate(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(this)
        formattedDate = SimpleDateFormat("dd-MM-yyyy").format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}


fun DataPoint.getStartTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getStartTime(TimeUnit.MILLISECONDS))

fun DataPoint.getEndTimeString(): String = DateFormat.getTimeInstance()
    .format(this.getEndTime(TimeUnit.MILLISECONDS))

// Convert String timestamp into string of date
fun String?.convertTimeStampToDate(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(this)
        formattedDate = SimpleDateFormat("EEEE, LLL dd").format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}

// Get local time from server timestamp
fun String?.convertTimeStampToTime(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(this)
        val df = SimpleDateFormat("hh:mm aaa", Locale.getDefault())
        formattedDate = df.format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}

// Get local time from server timestamp
fun String?.convertTimeStampToTim(): String? {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    var convertedDate: Date? = null
    var formattedDate: String? = null
    try {
        convertedDate = sdf.parse(this)
        val df = SimpleDateFormat("hh:mm aaa", Locale.getDefault())
        formattedDate = df.format(convertedDate)
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    return formattedDate
}


@SuppressLint("SimpleDateFormat")
fun String?.changeDatesFormat(sourceFormat: String?, targetFormat: String?): String? {
    // convert the Source string to Date object
    val formatter = SimpleDateFormat(sourceFormat)
    val date = this?.let { formatter.parse(it) }

    // Convert the Date object into desired format
    val desiredFormat = date?.let { SimpleDateFormat(targetFormat).format(it) }
    println(desiredFormat) //08, Aug 2019

    return desiredFormat
}

// Get Current Date
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentCal: Calendar = Calendar.getInstance()
    return dateFormat.format(currentCal.time)
}

// Get date after 30 days of current date
fun getDateAfter30Days(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val currentCal: Calendar = Calendar.getInstance()
    currentCal.add(Calendar.DATE, 30)
    return dateFormat.format(currentCal.time)
}