package com.shepherd.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventCommentUserDetailModel (
    @SerializedName("event_id") var event_id: Int? = null,
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id_details") var user_details: UserAssigneDetail = UserAssigneDetail(),
):Parcelable