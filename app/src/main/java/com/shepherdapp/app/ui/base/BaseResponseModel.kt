package com.shepherdapp.app.ui.base

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */

open class BaseResponseModel(
    @SerializedName("msg")
    open var message: String? = null,
    @SerializedName("error")
    var error: String? = null,

    @SerializedName("api_ver")
    var apiVersion: String? = null,

)