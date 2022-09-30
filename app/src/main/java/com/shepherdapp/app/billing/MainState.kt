package com.shepherdapp.app.billing

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase

/**
 * Created by Deepak Rattan on 30/09/22
 */
data class MainState(
    val hasRenewableBasic: Boolean? = false,
    val hasPrepaidBasic: Boolean? = false,
    val hasRenewablePremium: Boolean? = false,
    val hasPrepaidPremium: Boolean? = false,
    val basicProductDetails: ProductDetails? = null,
    val premiumProductDetails: ProductDetails? = null,
    val purchases: List<Purchase>? = null,
)
