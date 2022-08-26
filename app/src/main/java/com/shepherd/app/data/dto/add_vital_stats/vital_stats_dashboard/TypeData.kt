package com.shepherd.app.data.dto.add_vital_stats.vital_stats_dashboard
import com.google.gson.annotations.SerializedName


data class TypeData(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
)