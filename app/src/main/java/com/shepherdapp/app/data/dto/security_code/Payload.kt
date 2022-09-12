package com.shepherdapp.app.data.dto.security_code

import com.google.gson.annotations.SerializedName


data class Payload(
    @SerializedName("address")
    var address: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("deleted_at")
    var deletedAt: String? = null,
    @SerializedName("dob")
    var dob: String? = null,
    @SerializedName("firstname")
    var firstname: String? = null,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_biometric")
    var isBiometric: Boolean = false,
    @SerializedName("is_email_verified")
    var isEmailVerified: Boolean = false,
    @SerializedName("is_verified")
    var isVerified: Boolean = false,
    @SerializedName("lastname")
    var lastname: String? = null,
    @SerializedName("otp")
    var otp: String? = null,
    @SerializedName("phone_code")
    var phoneCode: String? = null,
    @SerializedName("phone_no")
    var phoneNo: String? = null,
    @SerializedName("profile_photo")
    var profilePhoto: String? = null,
    @SerializedName("security_code")
    var securityCode: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("user_id")
    var userId: Int = 0
)