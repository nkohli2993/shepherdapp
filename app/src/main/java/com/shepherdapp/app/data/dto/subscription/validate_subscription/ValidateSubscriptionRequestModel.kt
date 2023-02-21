package com.shepherdapp.app.data.dto.subscription.validate_subscription

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/12/22
 */

data class ValidateSubscriptionRequestModel(
    @SerializedName("purchaseToken") var purchaseToken: String? = null,
    @SerializedName("packageName") var packageName: String? = null,
    @SerializedName("productId") var productId: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null
)
