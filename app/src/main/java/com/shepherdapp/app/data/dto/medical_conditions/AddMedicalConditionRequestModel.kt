package com.shepherdapp.app.data.dto.medical_conditions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 13/09/22
 */
@Parcelize
data class AddMedicalConditionRequestModel(

    @SerializedName("condition") var condition: String? = null,
    @SerializedName("description") var description: String? = null
) : Parcelable
