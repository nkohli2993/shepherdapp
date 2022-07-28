package com.shepherd.app.data.local

import android.util.Log
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.dashboard.LoveUser
import com.shepherd.app.data.dto.login.Payload
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Const.LOVED_ONE_ID
import com.shepherd.app.utils.Const.LOVED_ONE_UUID
import com.shepherd.app.utils.Const.LOVED_USER_DETAILS
import com.shepherd.app.utils.Const.PAYLOAD
import com.shepherd.app.utils.Const.USER_DETAILS
import com.shepherd.app.utils.Const.USER_ID
import com.shepherd.app.utils.Const.USER_TOKEN
import com.shepherd.app.utils.Prefs
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */
class UserRepository @Inject constructor(private val apiService: ApiService) {

    fun getCurrentUser(): UserProfile? {
        return Prefs.with(ShepherdApp.appContext)!!.getObject(
            USER_DETAILS,
            UserProfile::class.java
        )
    }

    fun saveUser(user: UserProfile?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_DETAILS, user)
        Log.d("UserRepository", "User Info Saved to Preferences Successfully")
    }

    fun getLovedUser(): LoveUser? {
        return Prefs.with(ShepherdApp.appContext)!!.getObject(
            LOVED_USER_DETAILS,
            LoveUser::class.java
        )
    }

    fun saveLovedUser(user: LoveUser?) {
        Prefs.with(ShepherdApp.appContext)!!.save(LOVED_USER_DETAILS, user)
        Log.d("UserRepository", "User Info Saved to Preferences Successfully")
    }

    fun savePayload(payLoad: Payload?) {
        Prefs.with(ShepherdApp.appContext)!!.save(PAYLOAD, payLoad)
    }

    fun getPayload(): Payload? {
        return Prefs.with(ShepherdApp.appContext)?.getObject(PAYLOAD, Payload::class.java)
    }


    fun saveToken(token: String?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_TOKEN, token)
    }

    fun getToken() = Prefs.with(ShepherdApp.appContext)!!.getString(USER_TOKEN, "")

    fun saveUserId(id: Int) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_ID, id)
    }

    fun getUserId() = Prefs.with(ShepherdApp.appContext)!!.getInt(USER_ID)

    fun saveUUID(uuid: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(Const.UUID, uuid)
    }

    fun saveEmail(email: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(Const.EMAIL_ID, email)
    }

    fun getUUID() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")

    fun saveUserRole(role: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(Const.USER_ROLE, role)
    }

    fun getUserRole() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_ROLE, "")

    fun saveCareTeamLeaderUUID(uuid: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(Const.UUID, uuid)
    }

    fun saveLoggedInUserTeamLead(isLoggedInUserTeamLead: Boolean) {
        Prefs.with(ShepherdApp.appContext)!!
            .save(Const.Is_LOGGED_IN_USER_TEAM_LEAD, isLoggedInUserTeamLead)
    }

    fun isLoggedInUserTeamLead() =
        Prefs.with(ShepherdApp.appContext)?.getBoolean(Const.Is_LOGGED_IN_USER_TEAM_LEAD, false)

    fun getCareTeamLeaderUUID() =
        Prefs.with(ShepherdApp.appContext)!!.getString(Const.CARE_TEAM_LEADER_UUID, "")

    fun saveLovedOneId(id: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(LOVED_ONE_ID, id)
    }

    fun getLovedOneId() = Prefs.with(ShepherdApp.appContext)!!.getString(LOVED_ONE_ID)

    fun saveLovedOneUUId(uuid: String) {
        Prefs.with(ShepherdApp.appContext)!!.save(LOVED_ONE_UUID, uuid)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(LOVED_ONE_UUID, "")

    fun clearToken() {
        Prefs.with(ShepherdApp.appContext)!!.remove(USER_TOKEN)
    }

    fun clearData() {
        saveUser(null)
        saveToken(null)
    }

    fun clearSharedPref() {
        Prefs.with(ShepherdApp.appContext)?.removeAll()
    }


}