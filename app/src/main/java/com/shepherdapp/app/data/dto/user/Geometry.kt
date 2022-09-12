package com.shepherdapp.app.data.dto.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 09/09/22
 */
@Parcelize
data class Geometry(
    @SerializedName("type") var type: String? = null,
    @SerializedName("coordinates") var coordinates: ArrayList<Double> = arrayListOf()
) : Parcelable
