package com.shepherdapp.app.data.dto.care_team

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 08/06/22
 */

@Parcelize
data class Payload(

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("data") var data: ArrayList<CareTeamModel> = arrayListOf(),
//    @SerializedName("careroles") var careRoles: ArrayList<CareTeamRoles> = arrayListOf()
    @SerializedName("careteams") var careTeams: ArrayList<CareTeam> = arrayListOf(),
    @SerializedName("careroles") var careRoles: ArrayList<CareTeamRoles> = arrayListOf()
):Parcelable

