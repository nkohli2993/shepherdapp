package com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 04/02/23
 */
data class Payload(
    @SerializedName("total"        ) var total       : Int?             = null,
    @SerializedName("current_page" ) var currentPage : Int?             = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?             = null,
    @SerializedName("per_page"     ) var perPage     : Int?             = null,
    @SerializedName("users"        ) var users       : ArrayList<Users> = arrayListOf()

)
