package com.app.shepherd.data.dto.medical_conditions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 14/06/22
 */
data class PayloadMedicalConditions (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : Int?    = null,
    @SerializedName("condition_id" ) var conditionId : Int?    = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null

)
