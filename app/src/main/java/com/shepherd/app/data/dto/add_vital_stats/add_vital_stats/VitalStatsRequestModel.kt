package com.shepherd.app.data.dto.add_vital_stats.add_vital_stats

import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.med_list.schedule_medlist.Time

/**
 * Created by Nikita kohli on 05/08/22
 */

data class VitalStatsRequestModel(
    @SerializedName("love_user_id") var love_user_id: String? = null,
    @SerializedName("heart_rate") var heart_rate: String? = null,
    @SerializedName("time") var time: String? = null,


    )