package com.app.shepherd.data.dto.lock_box.update_lock_box

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 27/07/22
 */
data class UpdateLockBoxRequestModel(
    @SerializedName("name") val fileName: String? = null,
    @SerializedName("note") val note: String? = null
)
