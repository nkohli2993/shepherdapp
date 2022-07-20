package com.app.shepherd.data.dto.added_events
import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class EventDetailResponseModel(
    @SerializedName("payload") var payload: AddedEventModel
) : BaseResponseModel()