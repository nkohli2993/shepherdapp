package com.shepherd.app.data.dto.med_list.create_medlist
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita kohli on 01/08/22
 */
data class MedListPayload(
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("medlists")
    var medlists: ArrayList<MedListModel> = arrayListOf(),
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("total_pages")
    var totalPages: Int = 0
)