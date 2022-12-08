package com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard
import com.google.gson.annotations.SerializedName



data class GraphData(
    @SerializedName("time")
    var day: String = "",
    @SerializedName("x")
    var x: Double = 0.0,
    @SerializedName("x1")
    var x1: Double = 0.0,
    @SerializedName("y")
    var y: Double = 0.0,
    @SerializedName("y2")
    var y2: Double = 0.0
)