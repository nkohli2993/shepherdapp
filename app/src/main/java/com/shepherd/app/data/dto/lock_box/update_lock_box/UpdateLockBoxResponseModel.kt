package com.shepherd.app.data.dto.lock_box.update_lock_box

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 28/07/22
 */
data class UpdateLockBoxResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
