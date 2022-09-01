package com.shepherd.app.data.dto.add_vital_stats.add_vital_stats
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 01/09/22
 */

data class AddBloodPressureData(
    @SerializedName("sbp") var sbp: String? = null,
    @SerializedName("dbp") var dbp: String? = null,
)