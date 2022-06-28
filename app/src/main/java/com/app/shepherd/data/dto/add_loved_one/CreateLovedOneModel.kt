package com.app.shepherd.data.dto.add_loved_one

/**
 * Created by Deepak Rattan on 06/06/22
 */

import com.google.gson.annotations.SerializedName

data class CreateLovedOneModel(
    
    @SerializedName("email") var email: String? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("relation_id") var relationId: Int? = null,
    @SerializedName("phone_code") var phoneCode: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("place_id") var placeId: String? = null,
    @SerializedName("phone_no") var phoneNo: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null

)
