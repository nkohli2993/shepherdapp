package com.app.shepherd.data.dto.added_events

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class AllCommentEventsResponseModel(
    @SerializedName("payload") var payload: AllEventCommentModel
) : BaseResponseModel()