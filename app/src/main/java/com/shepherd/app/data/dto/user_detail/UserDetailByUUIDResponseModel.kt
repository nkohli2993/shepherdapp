package com.shepherd.app.data.dto.user_detail

import com.shepherd.app.data.dto.login.Payload
import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class UserDetailByUUIDResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
