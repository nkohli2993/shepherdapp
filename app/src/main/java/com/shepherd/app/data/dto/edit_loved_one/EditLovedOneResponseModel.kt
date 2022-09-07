package com.shepherd.app.data.dto.edit_loved_one

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 07/09/22
 */
data class EditLovedOneResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
