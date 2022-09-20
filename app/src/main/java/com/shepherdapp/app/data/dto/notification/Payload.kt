package com.shepherdapp.app.data.dto.notification

import com.google.gson.annotations.SerializedName

data class Payload (
    @SerializedName("total"        ) var total       : Int?            = null,
    @SerializedName("current_page" ) var currentPage : Int?            = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?            = null,
    @SerializedName("per_page"     ) var perPage     : Int?            = null,
    @SerializedName("data"         ) var data        : ArrayList<Data> = arrayListOf()

)