package com.shepherdapp.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AllEventCommentModel(
    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var current_page: String? = null,
    @SerializedName("total_pages") var total_pages: String? = null,
    @SerializedName("per_page") var per_page: String? = null,
    @SerializedName("data") var data: ArrayList<EventCommentUserDetailModel> = arrayListOf(),
):Parcelable