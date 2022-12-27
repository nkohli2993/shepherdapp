package com.shepherdapp.app.data.dto.subscription.validate_subscription

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/12/22
 */
data class Payload(
    @SerializedName("isSuccessful") var isSuccessful: Boolean? = null,
    @SerializedName("errorMessage") var errorMessage: String? = null,
    @SerializedName("payload") var payloadSubscription: PayloadSubscription? = PayloadSubscription()

)
