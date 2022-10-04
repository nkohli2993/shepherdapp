package com.shepherdapp.app.data.local

import android.util.Log
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.dashboard.LoveUser
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Const.LOVED_ONE_DETAIL
import com.shepherdapp.app.utils.Const.LOVED_ONE_ID
import com.shepherdapp.app.utils.Const.LOVED_ONE_UUID
import com.shepherdapp.app.utils.Const.LOVED_USER_DETAILS
import com.shepherdapp.app.utils.Const.PAYLOAD
import com.shepherdapp.app.utils.Const.USER_DETAILS
import com.shepherdapp.app.utils.Const.USER_ID
import com.shepherdapp.app.utils.Const.USER_TOKEN
import com.shepherdapp.app.utils.Prefs
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
    fun getUserEmail() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.EMAIL_ID, "")

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

    fun saveLovedOneUserDetail(userLovedOne: UserLovedOne) {
        Prefs.with(ShepherdApp.appContext)!!.save(LOVED_ONE_DETAIL, userLovedOne)
    }

    fun getLovedOneUserDetail(): UserLovedOne? {
        return Prefs.with(ShepherdApp.appContext)!!
            .getObject(LOVED_ONE_DETAIL, UserLovedOne::class.java)
    }

    fun saveFirebaseToken(firebaseToken: String) {
        Prefs.with(ShepherdApp.appContext)?.save(Const.FIREBASE_TOKEN, firebaseToken)
    }

    fun getFirebaseToken() = Prefs.with(ShepherdApp.appContext)?.getString(Const.FIREBASE_TOKEN)

    fun saveLoggedInUserAsLovedOne(isLoggedInUserLovedOne: Boolean) {
        Prefs.with(ShepherdApp.appContext)!!
            .save(Const.Is_LOGGED_IN_USER_LOVED_ONE, isLoggedInUserLovedOne)
    }

    fun isLoggedInUserLovedOne() =
        Prefs.with(ShepherdApp.appContext)?.getBoolean(Const.Is_LOGGED_IN_USER_LOVED_ONE, false)


}