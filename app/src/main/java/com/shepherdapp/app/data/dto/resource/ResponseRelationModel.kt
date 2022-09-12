package com.shepherdapp.app.data.dto.resource

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 01/09/22
 */

data class ResponseRelationModel(
    @SerializedName("payload") var payload: Payload? = Payload()
):BaseResponseModel()
