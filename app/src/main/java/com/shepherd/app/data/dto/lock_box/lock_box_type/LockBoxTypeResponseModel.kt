package com.shepherd.app.data.dto.lock_box.lock_box_type

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/07/22
 */
data class LockBoxTypeResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
