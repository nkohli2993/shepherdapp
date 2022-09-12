package com.shepherdapp.app.data.dto.med_list.schedule_medlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimeSelectedlist(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("is_ampm") var isAmPM: String? = null
):Parcelable