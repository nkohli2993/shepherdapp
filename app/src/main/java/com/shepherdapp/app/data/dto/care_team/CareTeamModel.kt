package com.shepherdapp.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CareTeamModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("image") var image: String? = null,
    @SerializedName("role_id") var role_id: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("love_user_id") var love_user_id: String? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("created_at") var created_at: String? = null,
//    @SerializedName("careteams") var careTeams: ArrayList<CareTeam> = arrayListOf(),
    @SerializedName("care_conditions") var careConditions: ArrayList<CareCondition> = arrayListOf(),
    @SerializedName("care_roles") var careRoles: CareTeamRoles = CareTeamRoles(),
    @SerializedName("user_id_details") var user_id_details: LoveUser = LoveUser(),
    @SerializedName("love_user_id_details") var love_user_id_details: LoveUser = LoveUser(),
    @SerializedName("relationship") var relationship: Relationship? = Relationship(),
    @SerializedName("is_selected") var isSelected: Boolean = false,
    @SerializedName("is_pending_invite") var isPendingInvite: Boolean = false,
    @SerializedName("status") var status: Boolean? = null,
    @SerializedName("is_invited") var isInvited: Boolean? = null,   // isInvited is true if the member is invited to join the careTeam
) : Parcelable