package com.app.shepherd.data.dto.forgot_password

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 02/06/22
 */

data class ForgotPasswordModel(
    @SerializedName("type") var type: String? = null,
    @SerializedName("email") var email: String? = null
)
