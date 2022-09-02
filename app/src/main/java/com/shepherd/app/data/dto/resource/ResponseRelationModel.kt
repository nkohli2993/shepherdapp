package com.shepherd.app.data.dto.resource

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 01/09/22
 */

data class ResponseRelationModel(
    @SerializedName("payload") var payload: Payload? = Payload()
):BaseResponseModel()
