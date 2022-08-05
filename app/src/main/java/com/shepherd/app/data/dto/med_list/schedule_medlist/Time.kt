package com.shepherd.app.data.dto.med_list.schedule_medlist

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/07/22
 */
data class Time(
    @SerializedName("time" ) var time : String? = null,
    @SerializedName("hour" ) var hour : String? = null
)
