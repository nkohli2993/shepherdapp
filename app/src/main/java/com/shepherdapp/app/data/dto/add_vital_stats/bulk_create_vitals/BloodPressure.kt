package com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/12/22
 */
data class BloodPressure(
    @SerializedName("dbp") var dbp: String? = null,
    @SerializedName("sbp") var sbp: String? = null
)
