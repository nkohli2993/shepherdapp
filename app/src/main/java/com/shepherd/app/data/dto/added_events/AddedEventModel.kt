package com.shepherd.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.care_team.LoveUser
import kotlinx.android.parcel.Parcelize

@Parcelize
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
    @SerializedName("created_at") var created_at: String? = null,
    @SerializedName("created_by") var created_by: String? = null,
    @SerializedName("id") var id: Int? = null,
    @SerializedName("event_comments") var event_comments: ArrayList<EventCommentsModel> = arrayListOf(),
    @SerializedName("user_assignes") var user_assignes: ArrayList<UserAssigneeModel> = arrayListOf(),
    @SerializedName("loved_one_user_id_details") var loved_one_user_id_details: LoveUser = LoveUser(),
    @SerializedName("created_by_details") var createdByDetails: CreatedByDetails? = CreatedByDetails(),
) : Parcelable