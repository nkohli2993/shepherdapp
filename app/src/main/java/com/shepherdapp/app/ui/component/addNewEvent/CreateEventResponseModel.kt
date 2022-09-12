package com.shepherdapp.app.ui.component.addNewEvent

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class CreateEventResponseModel(
    @SerializedName("payload") var payload: Any
) : BaseResponseModel()

