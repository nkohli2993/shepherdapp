package com.shepherdapp.app.data.dto.resource

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 12/08/22
 */
data class GetCategoriesResponseModel(
    @SerializedName("payload") var payload: CategoryPayload = CategoryPayload()
) : BaseResponseModel()