package com.shepherd.app.data.dto.resource

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 01-09-2022
 */

data class ResponseRelationModel(
    @SerializedName("payload") var payload: Payload? = Payload()
):BaseResponseModel()
