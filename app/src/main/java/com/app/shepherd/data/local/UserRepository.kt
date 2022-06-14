package com.app.shepherd.data.local

import android.util.Log
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.network.retrofit.ApiService
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


    fun saveToken(token: String?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_TOKEN, token)
    }

    fun getToken() = Prefs.with(ShepherdApp.appContext)!!.getString(USER_TOKEN, "")

    fun saveUserId(id: Int) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_ID, id)
    }

    fun getUserId() = Prefs.with(ShepherdApp.appContext)!!.getInt(USER_ID, 0)


    fun clearData() {
        saveUser(null)
        saveToken(null)
    }

    fun clearSharedPref() {
        Prefs.with(ShepherdApp.appContext)?.removeAll()
    }
}