package com.app.shepherd.data.dto.roles

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/06/22
 */
data class RolesResponseModel(
    @SerializedName("payload") val payload : Payload
):BaseResponseModel()
