package com.app.shepherd.data.dto.added_events
import com.google.gson.annotations.SerializedName

data class AddedEventModel(
    @SerializedName("loved_one_user_id") var loved_one_user_id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("notes") var notes: String? = null,
    @SerializedName("assign_to") var assign_to: ArrayList<Int>? = null,
    @SerializedName("is_active") var is_active: Boolean? = null,
    @SerializedName("deleted_at") var deleted_at: String? = null,
    @SerializedName("created_by") var created_by: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("event_comments") var event_comments: ArrayList<EventCommentsModel> = arrayListOf(),
    @SerializedName("user_assignes") var user_assignes: ArrayList<UserAssigneeModel> = arrayListOf(),
)
