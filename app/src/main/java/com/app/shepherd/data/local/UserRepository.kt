package com.app.shepherd.data.local

import android.util.Log
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.user.Payload
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Const.LOVED_ONE_ID
import com.app.shepherd.utils.Const.LOVED_ONE_UUID
import com.app.shepherd.utils.Const.PAYLOAD
import com.app.shepherd.utils.Const.USER_DETAILS
import com.app.shepherd.utils.Const.USER_ID
import com.app.shepherd.utils.Const.USER_TOKEN
import com.app.shepherd.utils.Prefs
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */
class UserRepository @Inject constructor(private val apiService: ApiService) {

    fun getCurrentUser(): UserProfiles? {
        return Prefs.with(ShepherdApp.appContext)!!.getObject(
            USER_DETAILS,
            UserProfiles::class.java
        )
    }

    fun saveUser(user: UserProfiles?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_DETAILS, user)
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

    fun getUUID() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")


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