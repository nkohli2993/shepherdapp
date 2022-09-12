package com.shepherdapp.app.data.dto.med_list.schedule_medlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FrequencyData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("time") var time: Int? = null,
):Parcelable