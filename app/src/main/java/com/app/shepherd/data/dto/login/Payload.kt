package com.app.shepherd.data.dto.login

import com.app.shepherd.data.dto.user.UserProfiles
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */
data class Payload(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("is_active") var isActive: Boolean? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("token") var token: String? = null,
    @SerializedName("user_roles") var userRoles: ArrayList<UserRoles?>? = arrayListOf(),
   // @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile(),
    @SerializedName("is_biometric") var isBiometric: Boolean? = null,

    @SerializedName("user_profile") var userProfile: UserProfiles? = UserProfiles()
)