package com.shepherdapp.app.data.dto.signup

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class BioMatricData(
    @SerializedName("device") var device: String? = null,
) : Parcelable