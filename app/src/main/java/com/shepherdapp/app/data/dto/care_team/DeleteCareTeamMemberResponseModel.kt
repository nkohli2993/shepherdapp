package com.shepherdapp.app.data.dto.care_team

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 01/07/22
 */
data class DeleteCareTeamMemberResponseModel(
    @SerializedName("payload") val payload: Boolean
) : BaseResponseModel()
