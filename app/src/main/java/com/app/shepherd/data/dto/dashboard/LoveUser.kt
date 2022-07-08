package com.app.shepherd.data.dto.dashboard

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/07/22
 */
data class LoveUser(

    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null

)
