package com.shepherd.app.data.dto.added_events
import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class EventDetailResponseModel(
    @SerializedName("payload") var payload: AddedEventModel
) : BaseResponseModel()