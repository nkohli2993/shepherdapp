package com.shepherd.app.data.dto.med_list.schedule_medlist

import com.google.gson.annotations.SerializedName

data class TimeSelectedlist(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("is_ampm") var isAmPM: String? = null
)