package com.shepherd.app.ui.component.addNewEvent

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class CreateEventResponseModel(
    @SerializedName("payload") var payload: Any
) : BaseResponseModel()

