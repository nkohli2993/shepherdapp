package com.app.shepherd.ui.base

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */

open class BaseResponseModel(
    @SerializedName("msg")
    var message: String? = null,

    @SerializedName("api_ver")
    var apiVersion: String? = null,

//    var statusCode: Int = 0,

//    @SerializedName("payLoad")
//    var payLoad: T? = null
)