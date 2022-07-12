package com.app.shepherd.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 12/07/22
 */
data class UserRole(
    @SerializedName("role_id") var roleID: Int? = null
)
