package com.shepherdapp.app.data.dto.med_list.loved_one_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 29/08/22
 */

@Parcelize
data class UserMedicationRemiderData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("assigned_by") var assignedBy: String? = null,
    @SerializedName("assigned_to") var assignedTo: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("selectedDate") var selectedDate: String? = null,
    @SerializedName("dosage_id") var dosageId: Int? = null,
    @SerializedName("medlist_id") var medlistId: Int? = null,
    @SerializedName("frequency") var frequency: String? = null,
    @SerializedName("days") var days: String? = null,
    @SerializedName("time") var time: Time ? = null,
    @SerializedName("note") var note: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("medlist") var medlist: com.shepherdapp.app.data.dto.med_list.Medlist? = com.shepherdapp.app.data.dto.med_list.Medlist(),
    @SerializedName("dosage") var dosage: com.shepherdapp.app.data.dto.med_list.Medlist? = com.shepherdapp.app.data.dto.med_list.Medlist(),
    @SerializedName("action_type") var actionType: Int? = null,
    @SerializedName("delete_position") var deletePosition: Int? = null,
    @SerializedName("isRecordAdded") var isRecordAdded: Boolean? = null,
) : Parcelable
