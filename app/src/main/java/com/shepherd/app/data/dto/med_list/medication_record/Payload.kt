package com.shepherd.app.data.dto.med_list.medication_record

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class Payload(
    @SerializedName("created_at") val created_at: String,
    @SerializedName("updated_at") val updated_at: String,
    @SerializedName("id") val id: Int,
    @SerializedName("user_medication_id") val user_medication_id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String,
    @SerializedName("user_id") val user_id: String
)