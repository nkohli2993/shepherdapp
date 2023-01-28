package com.shepherdapp.app.data.dto.subscription

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/11/22
 */
data class SubscriptionRequestModel(
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("plan") var plan: String? = null,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("expiry_date") var expiryDate: String? = null,
    @SerializedName("trial_start_at") var trialStartAt: String? = null,
    @SerializedName("trial_end_at") var trialEndAt: String? = null,
//    @SerializedName("status") var status: String? = null
)