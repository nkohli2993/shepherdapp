package com.shepherd.app.data.dto.care_team

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/07/22
 */
data class UpdateCareTeamMemberResponseModel(
    @SerializedName("payload") var payload: PayloadUpdateCareTeam
) : BaseResponseModel()
