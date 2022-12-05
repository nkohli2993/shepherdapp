package com.shepherdapp.app.data.dto.lock_box.share_lock_box

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 05/12/22
 */
data class ShareLockBoxResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
