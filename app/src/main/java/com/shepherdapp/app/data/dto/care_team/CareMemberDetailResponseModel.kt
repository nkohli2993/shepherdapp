package com.shepherdapp.app.data.dto.care_team

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Nikita 22/05/23
 */
data class CareMemberDetailResponseModel(
    @SerializedName("payload") var payload: CareTeamModel
) : BaseResponseModel()
