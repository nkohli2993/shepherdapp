package com.app.shepherd.data.dto.add_loved_one

import com.app.shepherd.ui.base.BaseResponseModel
import com.app.shepherd.ui.base.BaseViewModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/06/22
 */
data class UploadPicResponseModel(
    @SerializedName("profile_photo") var payload: Payload? = Payload()
) : BaseResponseModel()
