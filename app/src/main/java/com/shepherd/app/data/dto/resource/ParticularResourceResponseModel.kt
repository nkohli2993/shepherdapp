package com.shepherd.app.data.dto.resource

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 02/09/22
 */

data class ParticularResourceResponseModel(
    @SerializedName("payload") var payload: AllResourceData? = AllResourceData()
):BaseResponseModel()
