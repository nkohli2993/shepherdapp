package com.app.shepherd.data.dto.user_detail

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class UserRoles(
    @SerializedName("role_id") var roleId: Int? = null
)
