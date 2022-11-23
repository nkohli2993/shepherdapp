package com.shepherdapp.app.data.dto.enterprise

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 23/11/22
 */

data class AttachEnterpriseResponseModel(
    @SerializedName("payload") var payload: Boolean = false
) : BaseResponseModel()
