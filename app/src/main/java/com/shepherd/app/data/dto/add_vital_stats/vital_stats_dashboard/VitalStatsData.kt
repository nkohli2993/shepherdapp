package com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard

import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.AddVitalData

/**
 * Created by Nikita Kohli on 23/08/22
 */
data class VitalStatsData(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("loveone_user_id")
    var loveUserId: String? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("time")
    var time: String? = null,
    @SerializedName("data")
    var data: AddVitalData? = null,
)