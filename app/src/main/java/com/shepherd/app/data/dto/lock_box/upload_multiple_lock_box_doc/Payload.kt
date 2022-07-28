package com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/07/22
 */
data class Payload(
    @SerializedName("document") var document: ArrayList<String> = arrayListOf()
)
