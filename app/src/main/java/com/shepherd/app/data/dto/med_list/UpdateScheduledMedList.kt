package com.shepherd.app.data.dto.med_list

import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.med_list.schedule_medlist.Time

data class UpdateScheduledMedList(
    @SerializedName("dosage_id") var dosage_id: String? = null,
    @SerializedName("frequency") var frequency: String? = null,
    @SerializedName("days") var days: String? = null,
    @SerializedName("time") var time: ArrayList<Time> = arrayListOf(),
    @SerializedName("note") var note: String? = null,
    @SerializedName("end_date") var end_date: String? = null,

    )