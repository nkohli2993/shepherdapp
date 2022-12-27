package com.shepherdapp.app.data.dto.edit_event

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 08/12/22
 */
data class EditEventResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
