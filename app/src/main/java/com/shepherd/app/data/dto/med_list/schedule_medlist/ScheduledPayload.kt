package com.shepherd.app.data.dto.med_list.schedule_medlist
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ScheduledPayload(
    @SerializedName("assigned_by")
    var assignedBy: String = "",
    @SerializedName("assigned_to")
    var assignedTo: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("days")
    var days: String = "",
    @SerializedName("dosage_id")
    var dosageId: String = "",
    @SerializedName("frequency")
    var frequency: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("love_user_id")
    var loveUserId: String = "",
    @SerializedName("medlist_id")
    var medlistId: String = "",
    @SerializedName("note")
    var note: String = "",
    @SerializedName("time")
    var time: List<Time> = listOf(),
    @SerializedName("updated_at")
    var updatedAt: String = ""
):Parcelable