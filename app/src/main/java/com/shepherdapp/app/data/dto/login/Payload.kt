package com.shepherdapp.app.data.dto.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 27/05/22
 */
@Parcelize
data class Payload(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("uuid") var uuid: String? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfile? = UserProfile(),
    @SerializedName("user_loved_one") var userLovedOne: ArrayList<UserLovedOne> = arrayListOf(),
    @SerializedName("user_roles") var userRoles: ArrayList<UserRoles> = arrayListOf()

):Parcelable