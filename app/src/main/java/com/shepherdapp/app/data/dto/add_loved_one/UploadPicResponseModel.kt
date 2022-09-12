package com.shepherdapp.app.data.dto.add_loved_one

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class UploadPicResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
