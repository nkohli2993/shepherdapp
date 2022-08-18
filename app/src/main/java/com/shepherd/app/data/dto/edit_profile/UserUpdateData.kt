package com.shepherd.app.data.dto.edit_profile

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita Kohli on 17/058/22
 */

data class UserUpdateData(

    @SerializedName("email") var email: String? = null,
    @SerializedName("role_id") var roleId: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("device") var device: String? = null,

    )