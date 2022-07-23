package com.app.shepherd.data.dto.lock_box.upload_lock_box_doc

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/07/22
 */
data class UploadLockBoxDocResponseModel(
    @SerializedName("payload") var payload: Payload
):BaseResponseModel()
