package com.app.shepherd.data.dto.add_loved_one

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
/*data class Payload(
    @SerializedName("profile_photo") var profilePhoto: String? = null

)*/

data class Payload(
    @SerializedName("id"            ) var id           : Int?          = null,
    @SerializedName("email"         ) var email        : String?       = null,
    @SerializedName("password"      ) var password     : String?       = null,
    @SerializedName("unique_uuid"   ) var uniqueUuid   : String?       = null,
    @SerializedName("is_active"     ) var isActive     : Boolean?      = null,
    @SerializedName("created_at"    ) var createdAt    : String?       = null,
    @SerializedName("updated_at"    ) var updatedAt    : String?       = null,
    @SerializedName("deleted_at"    ) var deletedAt    : String?       = null,
    @SerializedName("user_profiles" ) var userProfiles : UserProfiles? = UserProfiles(),
    // Url of uploaded pic
    @SerializedName("profile_photo" ) var profilePhoto : String? = null

)