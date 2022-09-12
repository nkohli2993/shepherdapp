package com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard

import com.google.gson.annotations.SerializedName


/**
 * Created by Nikita Kohli on 23/08/22
 */

data class Payload(
    @SerializedName("latestOne") var latestOne: VitalStatsData? = null,
    @SerializedName("graphData") var graphData: ArrayList<GraphData> = arrayListOf(),
    @SerializedName("minAverage") var minAverage: Double? = null,
    @SerializedName("maxAverage") var maxAverage: Double? = null,
    /* @SerializedName("total") var total: Int? = null,
     @SerializedName("current_page") var currentPage: Int? = null,
     @SerializedName("total_pages") var totalPages: Int? = null,
     @SerializedName("per_page") var perPage: Int? = null,
     @SerializedName("data") var data: ArrayList<VitalStatsData> = arrayListOf(),*/

)

