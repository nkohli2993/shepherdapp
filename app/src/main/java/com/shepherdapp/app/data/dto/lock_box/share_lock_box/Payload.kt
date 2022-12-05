package com.shepherdapp.app.data.dto.lock_box.share_lock_box

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 05/12/22
 */
data class Payload(
    @SerializedName("documentUrl") var documentUrl: String? = null
)
