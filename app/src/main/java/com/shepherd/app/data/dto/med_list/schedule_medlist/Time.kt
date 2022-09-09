package com.shepherd.app.data.dto.med_list.schedule_medlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 28/07/22
 */
@Parcelize
data class Time(
    @SerializedName("time" ) var time : String? = null,
    @SerializedName("hour" ) var hour : String? = null
):Parcelable
