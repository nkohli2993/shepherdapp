package com.app.shepherd.data.dto.user_detail
import com.google.gson.annotations.SerializedName

/**
* Created by Deepak Rattan on 12/07/22
*/
data class Payload (

    @SerializedName("id"             ) var id           : Int?                    = null,
    @SerializedName("is_active"      ) var isActive     : Boolean?                = null,
    @SerializedName("email"          ) var email        : String?                 = null,
    @SerializedName("token"          ) var token        : String?                 = null,
    @SerializedName("uuid"           ) var uuid         : String?                 = null,
    @SerializedName("user_profiles"  ) var userProfiles : UserProfiles?           = UserProfiles(),
    @SerializedName("user_loved_one" ) var userLovedOne : ArrayList<UserLovedOne> = arrayListOf(),
    @SerializedName("user_roles"     ) var userRoles    : ArrayList<UserRoles>    = arrayListOf()

)
