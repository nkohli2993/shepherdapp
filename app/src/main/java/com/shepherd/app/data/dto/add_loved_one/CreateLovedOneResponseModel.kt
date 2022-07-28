package com.shepherd.app.data.dto.add_loved_one

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class CreateLovedOneResponseModel(
    @SerializedName("payload") var payload: Payload
) : BaseResponseModel()

