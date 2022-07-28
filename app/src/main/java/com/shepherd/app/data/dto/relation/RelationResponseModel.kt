package com.shepherd.app.data.dto.relation

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class RelationResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
):BaseResponseModel()
