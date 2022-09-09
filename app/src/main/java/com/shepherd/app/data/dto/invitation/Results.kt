package com.shepherd.app.data.dto.invitation

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 05/07/22
 */
@Parcelize
data class Results(

    /* @SerializedName("id"               ) var id             : Int?       = null,
     @SerializedName("user_id"          ) var userId         : Int?       = null,
     @SerializedName("receiver_user_id" ) var receiverUserId : Int?       = null,
     @SerializedName("email"            ) var email          : String?    = null,
     @SerializedName("loveone_user_id"  ) var loveoneUserId  : Int?       = null,
     @SerializedName("careteam_role_id" ) var careteamRoleId : Int?       = null,
     @SerializedName("uuid"             ) var uuid           : String?    = null,
     @SerializedName("permission"       ) var permission     : String?    = null,
     @SerializedName("status"           ) var status         : Int?       = null,
     @SerializedName("created_at"       ) var createdAt      : String?    = null,
     @SerializedName("updated_at"       ) var updatedAt      : String?    = null,
     @SerializedName("deleted_at"       ) var deletedAt      : String?    = null,
     @SerializedName("user"             ) var user           : User?      = User(),
     @SerializedName("love_user"        ) var loveUser       : LoveUser?  = LoveUser(),
     @SerializedName("care_roles"       ) var careRoles      : CareRoles? = CareRoles()*/

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("care_roles") var careRoles: CareRoles? = CareRoles(),
    var isSelected: Boolean = false

):Parcelable
