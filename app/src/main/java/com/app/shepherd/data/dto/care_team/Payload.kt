package com.app.shepherd.data.dto.care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/06/22
 */
data class Payload(

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("careteams") var careTeams: ArrayList<CareTeam> = arrayListOf(),
    @SerializedName("careroles") var careRoles: ArrayList<CareTeamRoles> = arrayListOf()
)
