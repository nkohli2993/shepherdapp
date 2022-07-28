package com.shepherd.app.data.dto.roles

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 28/06/22
 */

data class Payload(
    @SerializedName("total") val total: Int,
    @SerializedName("current_page") val current_page: Int,
    @SerializedName("total_pages") val total_pages: Int,
    @SerializedName("per_page") val per_page: Int,
    @SerializedName("users") val users: List<Users>
)
