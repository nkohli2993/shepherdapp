package com.shepherd.app.data.dto.med_list.loved_one_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 04/08/22
 */

@Parcelize
data class Payload(
    @SerializedName("user_medication_all") var userMedicationAll: ArrayList<Payload> = arrayListOf(),
    @SerializedName("id") var id: Int? = null,
    @SerializedName("assigned_by") var assignedBy: String? = null,
    @SerializedName("assigned_to") var assignedTo: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("dosage_id") var dosageId: Int? = null,
    @SerializedName("medlist_id") var medlistId: Int? = null,
    @SerializedName("frequency") var frequency: String? = null,
    @SerializedName("days") var days: String? = null,
    @SerializedName("time") var time: ArrayList<Time> = arrayListOf(),
    @SerializedName("note") var note: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("medlist") var medlist:  com.shepherd.app.data.dto.med_list.Medlist? = com.shepherd.app.data.dto.med_list.Medlist(),
    @SerializedName("dosage") var dosage:  com.shepherd.app.data.dto.med_list.Medlist? = com.shepherd.app.data.dto.med_list.Medlist(),

    //for function
    @SerializedName("action_type") var actionType: Int? = null,
    @SerializedName("delete_position") var deletePosition: Int? = null,

):Parcelable
