package com.app.shepherd.data.dto.added_events

import com.google.gson.annotations.SerializedName

data class CarePointsEvents (

    @SerializedName("total") var total: Int? = null,
    @SerializedName("current_page") var currentPage: Int? = null,
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("per_page") var perPage: Int? = null,
    @SerializedName("results") var results: ArrayList<ResultEventModel> = arrayListOf()
)