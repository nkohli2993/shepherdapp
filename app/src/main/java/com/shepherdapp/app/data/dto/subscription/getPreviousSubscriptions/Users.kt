package com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/02/23
 */
data class Users(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("plan") var plan: String? = null,
    @SerializedName("price_id") var priceId: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("card_id") var cardId: String? = null,
    @SerializedName("amount") var amount: Int? = null,
    @SerializedName("expiry_date") var expiryDate: String? = null,
    @SerializedName("allowed_loved_ones_count") var allowedLovedOnesCount: Int? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("trial_start_at") var trialStartAt: String? = null,
    @SerializedName("trial_end_at") var trialEndAt: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
