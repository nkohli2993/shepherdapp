package com.shepherdapp.app.data.dto.care_team

import com.google.gson.annotations.SerializedName

data class CareTeamPayLoad(

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("careteams") var careTeams: ArrayList<CareTeam> = arrayListOf(),
    @SerializedName("careroles") var careRoles: ArrayList<CareTeamRoles> = arrayListOf()
)
