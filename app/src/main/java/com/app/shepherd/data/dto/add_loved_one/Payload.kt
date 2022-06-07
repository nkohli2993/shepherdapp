package com.app.shepherd.data.dto.add_loved_one

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class Payload(
    @SerializedName("profile_photo") var profilePhoto: String? = null

)
