package com.shepherd.app.data.dto.med_list.schedule_medlist

import com.google.gson.annotations.SerializedName

data class DayList(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("is_selected") var isSelected: Boolean = false
)