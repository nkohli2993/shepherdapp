package com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard
import com.google.gson.annotations.SerializedName



data class GraphData(
    @SerializedName("time")
    var day: String = "",
    @SerializedName("x")
    var x: Int = 0,
    @SerializedName("x1")
    var x1: Int = 0,
    @SerializedName("y")
    var y: Int = 0,
    @SerializedName("y2")
    var y2: Int = 0
)