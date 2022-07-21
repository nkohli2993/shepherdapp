package com.app.shepherd.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class AllEventCommentModel(
    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var current_page: String? = null,
    @SerializedName("total_pages") var total_pages: String? = null,
    @SerializedName("per_page") var per_page: String? = null,
    @SerializedName("data") var data: ArrayList<EventCommentUserDetailModel> = arrayListOf(),
)