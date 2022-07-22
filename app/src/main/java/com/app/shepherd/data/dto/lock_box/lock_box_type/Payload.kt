package com.app.shepherd.data.dto.lock_box.lock_box_type

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 22/07/22
 */
data class Payload(

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("lock_box_types") var lockBoxTypes: ArrayList<LockBoxTypes> = arrayListOf()

)
