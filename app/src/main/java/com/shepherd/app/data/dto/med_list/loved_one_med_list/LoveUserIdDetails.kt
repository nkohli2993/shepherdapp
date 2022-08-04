package com.shepherd.app.data.dto.med_list.loved_one_med_list

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/08/22
 */
data class LoveUserIdDetails(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("uid") var uid: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("address") var address: String? = null

)
