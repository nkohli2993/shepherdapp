package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 29/11/22
 */
@Parcelize
data class ActiveSubscription(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("plan") var plan: String? = null,
    @SerializedName("transaction_id") var transactionId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("expiry_date") var expiryDate: String? = null,
    @SerializedName("allowed_loved_ones_count") var allowedLovedOnesCount: Int? = null,
    @SerializedName("is_active") var isActive: Boolean = false,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
) : Parcelable
