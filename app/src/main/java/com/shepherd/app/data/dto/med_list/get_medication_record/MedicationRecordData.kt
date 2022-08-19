package com.shepherd.app.data.dto.med_list.get_medication_record

import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.med_list.get_medication_detail.Payload


/**
 * Created by Nikita Kohli  19/08/22
 */
data class MedicationRecordData(
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("deleted_at")
    var deletedAt: String? = null,
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("user_medication_id")
    var userMedicationId: Int? = null,
    @SerializedName("user_medications")
    var userMedications: Payload? = null
)
