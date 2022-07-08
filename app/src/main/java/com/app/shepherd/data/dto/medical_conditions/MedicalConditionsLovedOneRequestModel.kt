package com.app.shepherd.data.dto.medical_conditions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 14/06/22
 */
data class MedicalConditionsLovedOneRequestModel(
    @SerializedName("condition_id") var conditionId: Int? = null,
    @SerializedName("user_id") var lovedOneUUID: String? = null
)
