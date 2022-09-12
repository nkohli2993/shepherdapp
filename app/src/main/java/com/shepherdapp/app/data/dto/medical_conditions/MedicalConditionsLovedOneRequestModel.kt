package com.shepherdapp.app.data.dto.medical_conditions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 14/06/22
 */
@Parcelize
data class MedicalConditionsLovedOneRequestModel(
    @SerializedName("condition_id") var conditionId: Int? = null,
    @SerializedName("user_id") var lovedOneUUID: String? = null
):Parcelable
