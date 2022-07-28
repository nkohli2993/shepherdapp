package com.shepherd.app.data.dto.lock_box.upload_lock_box_doc

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/07/22
 */
data class UploadLockBoxDocResponseModel(
    @SerializedName("payload") var payload: Payload
):BaseResponseModel()
