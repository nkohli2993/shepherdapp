package com.app.shepherd.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class UserAssigneeModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("event_id") var event_id: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("deleted_at") var deleted_at: String? = null,
    @SerializedName("user_details") var user_details: UserDetailAssigneModel = UserDetailAssigneModel(),

    )