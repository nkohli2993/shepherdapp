package com.shepherdapp.app.data.dto.security_code

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 24/08/22
 */

data class SendSecurityCodeRequestModel(
    @SerializedName("security_code") var securityCode: String? = null,
)