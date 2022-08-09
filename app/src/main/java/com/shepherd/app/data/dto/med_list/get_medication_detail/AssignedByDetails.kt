package com.shepherd.app.data.dto.med_list.get_medication_detail

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/08/22
 */
data class AssignedByDetails(

    @SerializedName("id") val id: Int,
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("profile_photo") val profile_photo: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String
)