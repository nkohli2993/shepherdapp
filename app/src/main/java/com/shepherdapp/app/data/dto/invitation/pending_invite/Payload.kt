package com.shepherdapp.app.data.dto.invitation.pending_invite

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.care_team.CareTeamModel

/**
 * Created by Deepak Rattan on 28/10/22
 */
data class Payload(

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("results") var results: ArrayList<CareTeamModel> = arrayListOf()

)
