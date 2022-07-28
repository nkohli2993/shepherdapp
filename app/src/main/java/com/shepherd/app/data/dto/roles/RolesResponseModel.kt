package com.shepherd.app.data.dto.roles

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/06/22
 */
data class RolesResponseModel(
    @SerializedName("payload") val payload : Payload
):BaseResponseModel()
