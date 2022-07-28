package com.shepherd.app.data.dto.add_loved_one

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/07/22
 */
data class UserProfiles(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("is_biometric") var isBiometric: Boolean? = null,
    @SerializedName("is_email_verified") var isEmailVerified: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null
)
