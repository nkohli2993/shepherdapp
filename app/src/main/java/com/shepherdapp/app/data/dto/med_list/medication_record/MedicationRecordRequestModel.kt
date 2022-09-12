package com.shepherdapp.app.data.dto.med_list.medication_record

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class MedicationRecordRequestModel(
    @SerializedName("user_medication_id") val user_medication_id: Int,
    @SerializedName("date") val date: String,
    @SerializedName("time") val time: String
)
