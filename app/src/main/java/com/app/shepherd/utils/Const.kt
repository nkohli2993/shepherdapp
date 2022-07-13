package com.app.shepherd.utils

/**
 * Created by Deepak Rattan on 27/05/22
 */
object Const {
    const val USER_DETAILS = "USER DETAILS"
    const val USER_TOKEN = "USER Token"
    const val USER_ID = "USER ID"
    const val UUID = "UUID"
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
    const val TERM_OF_USE = "TERM_OF_USE"

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