package com.shepherd.app.data.dto.med_list

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 01/08/22
 */
data class Payload(
    @SerializedName("total"        ) var total       : Int?                = null,
    @SerializedName("current_page" ) var currentPage : Int?                = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?                = null,
    @SerializedName("per_page"     ) var perPage     : Int?                = null,
    @SerializedName("medlists"     ) var medlists    : ArrayList<Medlist> = arrayListOf()
)
