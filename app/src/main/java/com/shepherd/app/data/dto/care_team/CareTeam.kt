package com.shepherd.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 08/06/22
 */
@Parcelize
data class CareTeam(
    @SerializedName("id")
    var id: Int? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("love_user_id")
    var loveUserId: String? = null,
    @SerializedName("role_id")
    var roleId: Int? = null,
    @SerializedName("permission")
    var permission: String? = null,
    @SerializedName("status")
    var status: Boolean? = null,
    @SerializedName("user")
    var user: User? = User(),
    @SerializedName("love_user")
    var loveUser: LoveUser? = LoveUser(),
    @SerializedName("care_roles")
    var careRoles: CareRoles? = CareRoles(),
    /*@SerializedName("care_conditions")
    var careConditions: ArrayList<CareCondition> = arrayListOf(),*/
    @SerializedName("is_selected") var isSelected: Boolean = false

) : Parcelable