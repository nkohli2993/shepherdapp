package com.app.shepherd.data.local

import android.util.Log
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.login.Payload
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.utils.Const.USER_DETAILS
import com.app.shepherd.utils.Const.USER_TOKEN
import com.app.shepherd.utils.Prefs
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */
class UserRepository @Inject constructor(private val apiService: ApiService) {

    fun getCurrentUser(): Payload? {
        return Prefs.with(ShepherdApp.appContext)!!.getObject(
            USER_DETAILS,
            Payload::class.java
        )
    }

    fun saveUser(user: Payload?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_DETAILS, user)
        Log.d("UserRepository", "User Info Saved to Preferences Successfully")
    }


    fun saveToken(token: String?) {
        Prefs.with(ShepherdApp.appContext)!!.save(USER_TOKEN, token)
    }

    fun getToken() = Prefs.with(ShepherdApp.appContext)!!.getString(USER_TOKEN, "")

    fun clearData() {
        saveUser(null)
        saveToken(null)
    }
}