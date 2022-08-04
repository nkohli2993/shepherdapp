package com.shepherd.app.data.dto.med_list.loved_one_med_list

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/08/22
 */
data class Time(
    @SerializedName("hour") var hour: String? = null,
    @SerializedName("time") var time: String? = null
)
