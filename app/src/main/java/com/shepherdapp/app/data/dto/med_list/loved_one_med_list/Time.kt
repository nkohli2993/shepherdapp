package com.shepherdapp.app.data.dto.med_list.loved_one_med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 08/08/22
 */

@Parcelize
data class Time(
    @SerializedName("hour") var hour: String? = null,
    @SerializedName("time") var time: String? = null
) : Parcelable
