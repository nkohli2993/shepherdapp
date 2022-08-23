package com.shepherd.app.data.dto.add_vital_stats.add_vital_stats

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 23/08/22
 */

data class AddVitalData(
    @SerializedName("heart_rate") var heartRate: String? = null,
    @SerializedName("blood_pressure") var bloodPressure: String? = null,
    @SerializedName("body_temp") var bodyTemp: String? = null,
    @SerializedName("oxygen") var oxygen: String? = null
)