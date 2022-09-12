package com.shepherdapp.app.data.dto.added_events

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CarePointEventDataModel
    (
    @SerializedName("date") var date: String? = null,
    @SerializedName("events") var events: ArrayList<AddedEventModel> = arrayListOf()
):Parcelable