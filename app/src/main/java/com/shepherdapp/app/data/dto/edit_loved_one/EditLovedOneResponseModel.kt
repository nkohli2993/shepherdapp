package com.shepherdapp.app.data.dto.edit_loved_one

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 07/09/22
 */
data class EditLovedOneResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
