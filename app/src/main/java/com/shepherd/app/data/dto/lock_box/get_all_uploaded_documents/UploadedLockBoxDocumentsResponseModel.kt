package com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class UploadedLockBoxDocumentsResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
