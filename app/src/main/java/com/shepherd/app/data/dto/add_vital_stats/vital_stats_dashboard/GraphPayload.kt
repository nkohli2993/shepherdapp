package com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard
import com.google.gson.annotations.SerializedName


data class GraphPayload(
    @SerializedName("api_ver")
    var apiVer: String = "",
    @SerializedName("msg")
    var msg: String = "",
    @SerializedName("payload")
    var payload: List<List<Any>> = listOf()
)