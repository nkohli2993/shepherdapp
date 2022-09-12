package com.shepherdapp.app.data.dto.user_detail

import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class UserDetailByUUIDResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
