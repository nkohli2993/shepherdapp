package com.shepherdapp.app.data.dto.subscription.validate_subscription

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/12/22
 */
data class PayloadSubscription(
    @SerializedName("startTimeMillis") var startTimeMillis: String? = null,
    @SerializedName("expiryTimeMillis") var expiryTimeMillis: String? = null,
    @SerializedName("autoRenewing") var autoRenewing: Boolean? = null,
    @SerializedName("priceCurrencyCode") var priceCurrencyCode: String? = null,
    @SerializedName("priceAmountMicros") var priceAmountMicros: String? = null,
    @SerializedName("countryCode") var countryCode: String? = null,
    @SerializedName("developerPayload") var developerPayload: String? = null,
    @SerializedName("cancelReason") var cancelReason: Int? = null,
    @SerializedName("orderId") var orderId: String? = null,
    @SerializedName("purchaseType") var purchaseType: Int? = null,
    @SerializedName("acknowledgementState") var acknowledgementState: Int? = null,
    @SerializedName("kind") var kind: String? = null
)
