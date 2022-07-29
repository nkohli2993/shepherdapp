package com.shepherd.app.data.dto.care_team

import com.google.gson.annotations.SerializedName

data class CareTeamModel(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("role_id") var role_id: Int? = null,
    @SerializedName("user_id") var user_id: String? = null,
    @SerializedName("love_user_id") var love_user_id: String? = null,
    @SerializedName("permission") var permission: String? = null,
    @SerializedName("created_at") var created_at: String? = null,

//    @SerializedName("careteams") var careTeams: ArrayList<CareTeam> = arrayListOf(),
    @SerializedName("care_roles") var careRoles: CareTeamRoles = CareTeamRoles(),
    @SerializedName("user_id_details") var user_id_details: LoveUser = LoveUser(),
    @SerializedName("love_user_id_details") var love_user_id_details: LoveUser = LoveUser(),
    @SerializedName("is_selected") var isSelected: Boolean = false
)