package com.shepherdapp.app.data.dto.add_loved_one

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 06/06/22
 */
/*data class Payload(
    @SerializedName("profile_photo") var profilePhoto: String? = null

)*/
@Parcelize
data class Payload(
    @SerializedName("id") var id: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("unique_uuid") var uniqueUuid: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles(),
    @SerializedName("is_biometric") var isBiometric: Boolean? = null,
    // Url of uploaded pic
    @SerializedName("profile_photo") var profilePhoto: String? = null

):Parcelable