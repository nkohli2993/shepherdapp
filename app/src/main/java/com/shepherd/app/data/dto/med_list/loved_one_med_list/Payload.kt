package com.shepherd.app.data.dto.med_list.loved_one_med_list

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/08/22
 */
data class Payload(
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
    @SerializedName("medlist") var medlist: Medlist? = Medlist(),
    @SerializedName("love_user_id_details") var loveUserIdDetails: LoveUserIdDetails? = LoveUserIdDetails()
)
