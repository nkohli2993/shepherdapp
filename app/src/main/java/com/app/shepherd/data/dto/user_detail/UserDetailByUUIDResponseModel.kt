package com.app.shepherd.data.dto.user_detail

import com.app.shepherd.data.dto.login.Payload
import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class UserDetailByUUIDResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
