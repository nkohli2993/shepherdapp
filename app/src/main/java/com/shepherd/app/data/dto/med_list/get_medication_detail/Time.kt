package com.shepherd.app.data.dto.med_list.get_medication_detail

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class Time(
    @SerializedName("hour") val hour: String,
    @SerializedName("time") val time: String
)