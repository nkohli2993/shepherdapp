package com.app.shepherd.data.dto.user

import com.app.shepherd.data.dto.login.UserLovedOne
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/06/22
 */
data class Payload(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("fb_id") var fbId: String? = null,
    @SerializedName("google_id") var googleId: String? = null,
    @SerializedName("apple_id") var appleId: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("phone_otp") var phoneOtp: String? = null,
    @SerializedName("email_otp") var emailOtp: Int? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("is_admin_approved") var isAdminApproved: Boolean? = null,
    @SerializedName("is_biometric") var isBiometric: Boolean? = null,
    @SerializedName("is_block") var isBlock: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles(),
    @SerializedName("user_loved_one") var userLovedOne: ArrayList<UserLovedOne> = arrayListOf()

)
