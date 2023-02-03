package com.shepherdapp.app.data.dto.lock_box.create_lock_box

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.login.UserRoles
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 03/02/23
 */
@Parcelize
data class AllowedUsers(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("unique_uuid") var uniqueUuid: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfile? = UserProfile(),
    @SerializedName("user_loved_one") var userLovedOne: ArrayList<UserLovedOne> = arrayListOf(),
    @SerializedName("user_roles") var userRoles: ArrayList<UserRoles> = arrayListOf()
) : Parcelable
