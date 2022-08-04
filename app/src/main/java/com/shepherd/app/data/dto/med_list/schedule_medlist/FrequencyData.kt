package com.shepherd.app.data.dto.med_list.schedule_medlist

import com.google.gson.annotations.SerializedName


data class FrequencyData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("time") var time: Int? = null,
)