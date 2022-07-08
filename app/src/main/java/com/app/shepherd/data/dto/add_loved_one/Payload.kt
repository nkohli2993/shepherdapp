package com.app.shepherd.data.dto.add_loved_one

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
/*data class Payload(
    @SerializedName("profile_photo") var profilePhoto: String? = null

)*/

data class Payload(
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("fb_id") var fbId: String? = null,
    @SerializedName("google_id") var googleId: String? = null,
    @SerializedName("apple_id") var appleId: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("phone_otp") var phoneOtp: String? = null,
    @SerializedName("email_otp") var emailOtp: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("is_admin_approved") var isAdminApproved: Boolean? = null,
    @SerializedName("is_biometric") var isBiometric: Boolean? = null,
    @SerializedName("is_block") var isBlock: Boolean? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("password") var password: String? = null
)