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

        val headers: Headers = request.headers.newBuilder()
            .add("Content-Type", "application/json")
            .add("Accept", "application/json")

            .add(
                "Authorization",
                "${
                    if (sharedPrefUtils != null) sharedPrefUtils?.getString(
                        Const.USER_TOKEN,
                        ""
                    ) else "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjo0NjksInV1aWQiOiIzNmU5MWYzYi1jNGE0LTQwZDMtOWQ3MC01ODY1NGQyZGY1NTMiLCJlbWFpbCI6ImNhckB5b3BtYWlsLmNvbSIsImlhdCI6MTY4MTkwODc3M30.f3K-ZQ7CH8Ydejm3urhcqDCSb3rpAtM7W99h4bRsp-E"
                }"
            )
            .add(
                "device-id", if (sharedPrefUtils != null) {
                    sharedPrefUtils.getString(Const.DEVICE_ID, "") + sharedPrefUtils.getString(
                        Const.EMAIL_ID,
                        ""
                    )
                } else "1ba0ac521d0b2578car@yopmail.com"
            )
            .add(
                "fcm-token", if (sharedPrefUtils != null) {
                    sharedPrefUtils.getString(Const.FIREBASE_TOKEN)!!
                } else "ewCuReisTOCmNQ4E_3M0Y1:APA91bEKgyTiw62SYIhP9VCZq5z3e-U_E_fQrAeeYtde_RunGtYlejg9F8PjapWzXkL52bmSsiPgfcpMNe12h77F-OqYFukf2HgCcY6bKWsAgYf_0apxCXg_w_OTAQPjQm8VS1I13Bci"
            )
            .add("device-type", "0")
            .build()


        request = request.newBuilder().headers(headers).build()

        return chain.proceed(request)
    }
}