package com.shepherd.app.data.dto.lock_box.edit_lock_box

import com.google.gson.annotations.SerializedName

data class DocumentData(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("file_path") val filePath: String = "",
    @SerializedName("upload_date") val uploadDate: String = "",
    @SerializedName("new_added") val newAdded: Boolean = false
)