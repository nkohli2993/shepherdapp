package com.app.shepherd.data.dto.lock_box.create_lock_box

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class AddNewLockBoxResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
