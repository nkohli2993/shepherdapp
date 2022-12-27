package com.shepherdapp.app.data.dto.subscription.get_active_subscriptions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 24/11/22
 */
data class Payload(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("plan") var plan: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("amount") var amount: Double? = null,
    @SerializedName("expiry_date") var expiryDate: String? = null,
    @SerializedName("allowed_loved_ones_count") var allowedLovedOnesCount: Int? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
