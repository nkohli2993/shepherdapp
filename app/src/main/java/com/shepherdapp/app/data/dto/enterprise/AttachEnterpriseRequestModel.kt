package com.shepherdapp.app.data.dto.enterprise

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/11/22
 */

data class AttachEnterpriseRequestModel(
    @SerializedName("enterprise_code") var enterPriseCode: String? = null
)
