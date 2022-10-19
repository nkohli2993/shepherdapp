package com.shepherdapp.app.data.dto.med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.Time
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateScheduledMedList(
    @SerializedName("dosage_id") var dosage_id: String? = null,
    @SerializedName("dosage_type_id") var dosage_type_id: String? = null,
    @SerializedName("frequency") var frequency: String? = null,
    @SerializedName("days") var days: String? = null,
    @SerializedName("time") var time: ArrayList<Time>? = arrayListOf(),
    @SerializedName("note") var note: String? = null,
    @SerializedName("end_date") var end_date: String? = null,
    @SerializedName("isTimeChanged") var isTimeChanged: Boolean? = null,
    @SerializedName("isDoseChanged") var isDoseChanged: Boolean? = null,
    @SerializedName("date") var date: String? = null

):Parcelable