package com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 25/07/22
 */
data class Payload(
    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("lock_box") var lockBox: ArrayList<LockBox> = arrayListOf()
)
