package com.shepherd.app.data.dto.med_list.get_medication_detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 09/08/22
 */
@Parcelize
data class Time(
    @SerializedName("hour") val hour: String,
    @SerializedName("time") val time: String
):Parcelable