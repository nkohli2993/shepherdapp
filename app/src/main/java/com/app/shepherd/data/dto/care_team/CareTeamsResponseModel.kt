package com.app.shepherd.data.dto.care_team

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/06/22
 */
data class CareTeamsResponseModel(
    @SerializedName("payload") var payload: Payload
) : BaseResponseModel()
