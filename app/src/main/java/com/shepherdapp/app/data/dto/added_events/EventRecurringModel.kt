package com.shepherdapp.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventRecurringModel(
    @SerializedName("type") var type: String? = null,
    @SerializedName("value") var value: ArrayList<Int>? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("type_value") var typeValue:String? = null
): Parcelable