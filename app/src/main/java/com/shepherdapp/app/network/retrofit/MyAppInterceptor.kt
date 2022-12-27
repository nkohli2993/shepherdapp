package com.shepherdapp.app.network.retrofit

import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */

class MyAppInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val headers = request.headers.newBuilder()
            .add("Content-Type", "application/json")
            .add("Accept", "application/json")
            .add(
                "Authorization",
                "${Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_TOKEN, "")}"
            )
            .add(
                "device-id",
                "${
                    Prefs.with(ShepherdApp.appContext)!!.getString(Const.DEVICE_ID, "")
                }" + "${Prefs.with(ShepherdApp.appContext)!!.getString(Const.EMAIL_ID, "")}"
            )
            .add(
                "fcm-token",
                "${Prefs.with(ShepherdApp.appContext)?.getString(Const.FIREBASE_TOKEN)}"
            )
            .add("deviceType", "0")
            .build()

        request = request.newBuilder().headers(headers).build()

        return chain.proceed(request)
    }
}