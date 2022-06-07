package com.app.shepherd.data.dto.relation

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class RelationResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
):BaseResponseModel()
