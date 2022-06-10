package com.app.shepherd.data.dto.care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/06/22
 */
data class User(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_profiles") var userProfiles: UserProfiles? = UserProfiles()

)
