package com.shepherd.app.data.dto.care_team

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 08/06/22
 */
data class CareTeamsResponseModel(
    @SerializedName("payload") var payload: Payload
) : BaseResponseModel()
