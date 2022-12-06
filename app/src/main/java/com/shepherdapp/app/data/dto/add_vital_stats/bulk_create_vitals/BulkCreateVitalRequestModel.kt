package com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/12/22
 */
data class BulkCreateVitalRequestModel(
    @SerializedName("vital_stats" ) var vitalStats : ArrayList<VitalStats> = arrayListOf()
)
