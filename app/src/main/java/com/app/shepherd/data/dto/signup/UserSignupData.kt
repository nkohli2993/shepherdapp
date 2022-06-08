package com.app.shepherd.data.dto.signup

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/05/22
 */

data class UserSignupData (

    @SerializedName("email"         ) var email        : String? = null,
    @SerializedName("password"      ) var password     : String? = null,
    @SerializedName("role_id"       ) var roleId       : String? = null,
    @SerializedName("firstname"     ) var firstname    : String? = null,
    @SerializedName("lastname"      ) var lastname     : String? = null,
    @SerializedName("phone_code"    ) var phoneCode    : String? = null,
    @SerializedName("phone_no"      ) var phoneNo      : String? = null,
    @SerializedName("profile_photo" ) var profilePhoto : String? = null

)