package com.app.shepherd.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 09/06/22
 */
data class UserProfiles(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("user_id") var userId: Int? = null,
    @SerializedName("firstname") var firstname: String? = null,
    @SerializedName("lastname") var lastname: String? = null,
    @SerializedName("personal_statement") var personalStatement: String? = null,
    @SerializedName("gender") var gender: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("business_address") var businessAddress: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("zipcode") var zipcode: String? = null,
    @SerializedName("business_phone_code") var businessPhoneCode: String? = null,
    @SerializedName("business_phone") var businessPhone: String? = null,
    @SerializedName("profile_photo") var profilePhoto: String? = null,
    @SerializedName("mission_statement") var missionStatement: String? = null,
    @SerializedName("allow_mobile_visit") var allowMobileVisit: Int? = null,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("deleted_at") var deletedAt: String? = null

)
