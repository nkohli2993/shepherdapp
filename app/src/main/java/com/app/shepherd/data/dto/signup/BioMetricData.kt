package com.app.shepherd.data.dto.signup

import com.google.gson.annotations.SerializedName

data class BioMetricData(
    @SerializedName("is_biometric") var isBiometric: Boolean? = null
)
