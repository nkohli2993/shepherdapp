package com.shepherdapp.app.data.dto.subscription.purchase

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 03/01/23
 */
data class PurchaseJson(
    @SerializedName("orderId") var orderId: String? = null,
    @SerializedName("packageName") var packageName: String? = null,
    @SerializedName("productId") var productId: String? = null,
    @SerializedName("purchaseTime") var purchaseTime: Int? = null,
    @SerializedName("purchaseState") var purchaseState: Int? = null,
    @SerializedName("purchaseToken") var purchaseToken: String? = null,
    @SerializedName("quantity") var quantity: Int? = null,
    @SerializedName("autoRenewing") var autoRenewing: Boolean? = null,
    @SerializedName("acknowledged") var acknowledged: Boolean? = null
)
