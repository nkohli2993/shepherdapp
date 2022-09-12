package com.shepherdapp.app.data.dto.added_events

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

data class AllCommentEventsResponseModel(
    @SerializedName("payload") var payload: AllEventCommentModel
) : BaseResponseModel()