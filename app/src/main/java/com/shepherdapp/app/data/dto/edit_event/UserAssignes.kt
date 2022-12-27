package com.shepherdapp.app.data.dto.edit_event

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/12/22
 */
data class UserAssignes(
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("event_id") var eventId: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
