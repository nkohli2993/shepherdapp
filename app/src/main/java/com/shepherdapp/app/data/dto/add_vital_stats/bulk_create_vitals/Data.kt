package com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/12/22
 */
data class Data(
    @SerializedName("body_temp"      ) var bodyTemp      : String?        = null,
    @SerializedName("blood_pressure" ) var bloodPressure : BloodPressure? = BloodPressure(),
    @SerializedName("heart_rate"     ) var heartRate     : String?        = null,
    @SerializedName("oxygen"         ) var oxygen        : String?        = null

)
