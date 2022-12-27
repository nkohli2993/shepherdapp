package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 21/06/22
 */
@Parcelize
data class UserLovedOne(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("love_user_id") var loveUserId: String? = null,
    @SerializedName("role_id") var roleId: Int? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("care_roles") var careRoles: CareRoles? = CareRoles(),
    @SerializedName("first_name") var firstName: String? = null,
    @SerializedName("last_name") var lastName: String? = null,
    @SerializedName("profile_pic") var profilePic: String? = null
) : Parcelable
