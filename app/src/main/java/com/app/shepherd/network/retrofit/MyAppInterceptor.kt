package com.app.shepherd.network.retrofit

import com.app.shepherd.ShepherdApp
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
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
                "device",
                "${Prefs.with(ShepherdApp.appContext)!!.getString(Const.DEVICE_ID, "")}"
            )
            .build()

        request = request.newBuilder().headers(headers).build()

        return chain.proceed(request)
    }
}