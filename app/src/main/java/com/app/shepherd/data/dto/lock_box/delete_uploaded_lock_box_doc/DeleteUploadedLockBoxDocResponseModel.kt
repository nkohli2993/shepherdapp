package com.app.shepherd.data.dto.lock_box.delete_uploaded_lock_box_doc

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class DeleteUploadedLockBoxDocResponseModel(
    @SerializedName("payload") val payload: Boolean = false
) : BaseResponseModel()
