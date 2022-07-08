package com.app.shepherd.data.dto.invitation

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 05/07/22
 */
data class LoveUser(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles()
)
