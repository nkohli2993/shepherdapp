package com.shepherdapp.app.utils

/**
 * Created by Deepak Rattan on 27/05/22
 */
object Const {
    const val USER_DETAILS = "USER DETAILS"
    const val USER_TOKEN = "USER Token"
    const val USER_ID = "USER ID"
    const val UUID = "UUID"
    const val CARE_TEAM_LEADER_UUID = "CARE TEAM LEADER UUID"
    const val Is_LOGGED_IN_USER_TEAM_LEAD = "Is_LOGGED_IN_USER_TEAM_LEAD"
    const val Is_LOGGED_IN_USER_LOVED_ONE = "Is_LOGGED_IN_USER_LOVED_ONE"
    const val Is_USER_ATTACHED_TO_ENTERPRISE = "Is USER ATTACHED TO ENTERPRISE"
    const val EMAIL_ID = "EMAIL_ID"
    const val USER_ROLE = "USER_ROLE"
    const val PAYLOAD = "PAYLOAD"
    const val DEVICE_ID = "DEVICE_ID"
    const val BIOMETRIC_ENABLE = "BIOMETRIC_ENABLE"
    const val SECOND_TIME_LOGIN = "SECOND_TIME_LOGIN"
    const val CARE_POINT = "CARE_POINT"
    const val CHAT = "CHAT"
    const val LOVED_ONE_ID = "LOVED_ONE_ID"
    const val LOVED_ONE_UUID = "LOVED_ONE_UUID"
    const val LOVED_ONE_ARRAY = "LOVED_ONE_ARRAY"
    const val PRIVACY_POLICY = "PRIVACY_POLICY"
    const val ABOUT_US = "ABOUT_US"
    const val TERM_OF_USE = "TERM_OF_USE"
    const val ADD_LOVE_ONE = "ADD_LOVE_ONE"
    const val LOVED_USER_DETAILS = "LOVED USER DETAILS"
    const val MEDICAL_CONDITION = "MEDICAL CONDITION"
    const val LOVED_ONE_DETAIL = "LOVED ONE DETAIL"
    const val RESET_SECURITY_CODE = "RESET"
    const val SET_SECURITY_CODE = "SET"
    const val FIREBASE_TOKEN = "FIREBASE_TOKEN"


    object NotificationAction {
        const val ADD_COMMENT = "post_comment"
        const val ADD_COMMENT_REPLY = "comment_reply"
        const val FOLLOW = "follow"
        const val LIKE_COMMENT = "comment_like"
        const val LIKE_POST = "post_like"
        const val MESSAGE = "message"
        const val AUDIO_CALL = "Audio_call"
        const val VIDEO_CALL = "Video_call"
        const val CARE_POINT_CREATED = "carepoint_created"
        const val MEDICATION_CREATED = "user_medication_created"
        const val MEDICATION_UPDATED = "user_medication_updated"
        const val LOCK_BOX_CREATED = "lockbox_created"
        const val LOCK_BOX_UPDATED = "lockbox_updated"
        const val CARE_TEAM_INVITE = "care_team_invite"
    }

    object VitalStat {
        const val BLOOD_PRESSURE = "blood_pressure"
        const val HEART_RATE = "heart_rate"
        const val BODY_TEMP = "body_temp"
        const val OXYGEN = "oxygen"
    }

    object Type {
        const val SUBSCRIPTION = "subscription"
        const val ENTERPRISE = "enterprise"
    }

    object SubscriptionPlan {
        const val ONE_WEEK = "One Week"
        const val ONE_MONTH = "One Month"
        const val ONE_YEAR = "One Year"
    }
}

object Drawable {
    const val END = 2
    const val TOP = 1
    const val START = 0
    const val BOTTOM = 3
}


enum class Modules(val value: Int) {
    CarePoints(1),
    LockBox(2),
    MedList(3),
    Resources(4)
}

enum class Role(val id: String) {
    User("1"),
    UserLovedOne("2"),
    Admin("3")
}

enum class UserSlug(val slug: String) {
    User("user"),
    UserLovedOne("user-loved-one"),
    Admin("admin")
}

enum class Invitations(val sendType: String) {
    Sender("sender"),
    Receiver("receiver")
}

enum class Status(val status: Int) {
    Zero(0),
    One(1)
}

enum class CareRole(val slug: String) {
    CareTeamLead("care_team_lead")
}

enum class CalendarState(val value: Int) {
    Today(1),
    Week(2),
    Month(3)
}

enum class ClickType(val value: Int) {
    View(1),
    Delete(2),
    Edit(3)
}

enum class MedListAction(val value: Int) {
    View(1),
    EDIT(2),
    Delete(3)
}

enum class FrequencyType(val value: String) {
    ONCE("1"),
    TWICE("2"),
    THRICE("3"),
    FOUR("4"),
    FIVE("5")
}

enum class TimePickerType(val value: Int) {
    ADD(1),
    EDIT(2)
}


