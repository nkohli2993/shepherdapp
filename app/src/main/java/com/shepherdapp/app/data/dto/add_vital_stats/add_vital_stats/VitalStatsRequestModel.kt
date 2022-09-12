package com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 05/08/22
 */

data class VitalStatsRequestModel(
    @SerializedName("loveone_user_id") var loveone_user_id: String? = null,
    @SerializedName("date") var heart_rate: String? = null,
    @SerializedName("time") var time: String? = null,
    @SerializedName("data") var data: AddVitalData? = null
)