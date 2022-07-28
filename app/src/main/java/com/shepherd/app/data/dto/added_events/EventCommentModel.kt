package com.shepherd.app.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class EventCommentModel (
    @SerializedName("event_id") var event_id: Int? = null,
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("updated_at") var updated_at: String? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("id") var id: Int? = null,
)