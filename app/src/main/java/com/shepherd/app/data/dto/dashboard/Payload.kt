package com.shepherd.app.data.dto.dashboard

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 29/06/22
 */
data class Payload(
    @SerializedName("carePoints"          ) var carePoints          : Int?                        = null,
    @SerializedName("medLists"            ) var medLists            : Int?                        = null,
    @SerializedName("lockBoxs"            ) var lockBoxs            : Int?                        = null,
    @SerializedName("careTeams"           ) var careTeams           : Int?                        = null,
    @SerializedName("lovedOneUserProfile" ) var lovedOneUserProfile : String?                     = null,
    @SerializedName("careTeamProfiles"    ) var careTeamProfiles    : ArrayList<CareTeamProfiles> = arrayListOf()
)
