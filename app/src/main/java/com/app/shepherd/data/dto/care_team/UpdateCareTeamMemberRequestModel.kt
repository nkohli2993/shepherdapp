package com.app.shepherd.data.dto.care_team

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/07/22
 */
data class UpdateCareTeamMemberRequestModel(
    @SerializedName("permission") var permission: String? = null
)
