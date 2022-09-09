package com.shepherd.app.data.dto.med_list.schedule_medlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DayList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("is_selected") var isSelected: Boolean = false,
    @SerializedName("is_clickable") var isClickabled: Boolean = false
):Parcelable