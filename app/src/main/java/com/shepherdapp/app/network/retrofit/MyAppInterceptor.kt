package com.shepherdapp.app.network.retrofit

import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */

class MyAppInterceptor @Inject constructor(
    private val sharedPrefUtils: Prefs?

) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()

        val headers : Headers = request.headers.newBuilder()
                .add("Content-Type", "application/json")
                .add("Accept", "application/json")

                .add(
                    "Authorization",
                    "${sharedPrefUtils?.getString(Const.USER_TOKEN, "")}"
                )
                .add(
                    "device-id",
                    "${
                        sharedPrefUtils?.getString(Const.DEVICE_ID, "")
                    }" + "${sharedPrefUtils?.getString(Const.EMAIL_ID, "")}"
                )
                .add(
                    "fcm-token",
                    "${sharedPrefUtils?.getString(Const.FIREBASE_TOKEN)}"
                )
                .add("device-type", "0")
                .build()


        request = request.newBuilder().headers(headers).build()

        return chain.proceed(request)
    }
}