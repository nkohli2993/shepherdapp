package com.shepherd.app.data.dto.medical_conditions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 14/06/22
 */
@Parcelize
data class PayloadMedicalConditions (

    @SerializedName("id"           ) var id          : Int?    = null,
    @SerializedName("user_id"      ) var userId      : String?    = null,
    @SerializedName("condition_id" ) var conditionId : Int?    = null,
    @SerializedName("created_at"   ) var createdAt   : String? = null,
    @SerializedName("updated_at"   ) var updatedAt   : String? = null

):Parcelable
