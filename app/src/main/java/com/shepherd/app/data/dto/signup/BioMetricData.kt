package com.shepherd.app.data.dto.signup

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BioMetricData(
    @SerializedName("is_biometric") var isBiometric: Boolean? = null
):Parcelable
