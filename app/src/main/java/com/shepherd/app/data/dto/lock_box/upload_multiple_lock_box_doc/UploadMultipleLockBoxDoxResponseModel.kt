package com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 28/07/22
 */
data class UploadMultipleLockBoxDoxResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
