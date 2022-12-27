package com.shepherdapp.app.data.dto.subscription.check_subscription_status

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/11/22
 */
data class Payload(
    @SerializedName("type") var type: String? = null,
    @SerializedName("status") var status: Boolean? = null
)
