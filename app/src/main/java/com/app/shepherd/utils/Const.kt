package com.app.shepherd.utils

/**
 * Created by Deepak Rattan on 27/05/22
 */
object Const {
    const val USER_DETAILS = "USER DETAILS"
    const val USER_TOKEN = "USER Token"
    const val USER_ID = "USER ID"
    const val PAYLOAD = "PAYLOAD"
    const val DEVICE_ID = "DEVICE_ID"
    const val BIOMETRIC_ENABLE = "BIOMETRIC_ENABLE"
    const val SECOND_TIME_LOGIN = "SECOND_TIME_LOGIN"
    const val LOVED_ONE_ID = "LOVED_ONE_ID"
    const val LOVED_ONE_ARRAY = "LOVED_ONE_ARRAY"
}

object Drawable {
    const val END = 2
    const val TOP = 1
    const val START = 0
    const val BOTTOM = 3
}


enum class Modules(val value: Int) {
    CareTeam(1),
    LockBox(2),
    MedList(3),
    Resources(4)
}