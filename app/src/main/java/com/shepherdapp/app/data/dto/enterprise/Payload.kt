package com.shepherdapp.app.data.dto.enterprise

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.login.Enterprise

/**
 * Created by Deepak Rattan on 16/12/22
 */
data class Payload(
    @SerializedName("enterprise") var enterprise: Enterprise? = Enterprise()
)
