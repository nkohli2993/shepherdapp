package com.app.shepherd.data.dto.lock_box.upload_lock_box_doc

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/07/22
 */
data class Payload(
    @SerializedName("document") var document: String? = null
)
