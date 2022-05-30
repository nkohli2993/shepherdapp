package com.app.shepherd.data.dto.login

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */
data class LoginResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
