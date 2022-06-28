package com.app.shepherd.ui.component.addNewEvent

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class CreateEventResponseModel(
    @SerializedName("payload") var payload: Any
) : BaseResponseModel()

