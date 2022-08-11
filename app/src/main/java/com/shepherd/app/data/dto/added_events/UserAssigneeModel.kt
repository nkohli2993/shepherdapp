package com.shepherd.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserAssigneeModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("event_id") var event_id: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("deleted_at") var deleted_at: String? = null,
    @SerializedName("user_id_details") var user_details: UserAssigneDetail = UserAssigneDetail(),

    ):Parcelable