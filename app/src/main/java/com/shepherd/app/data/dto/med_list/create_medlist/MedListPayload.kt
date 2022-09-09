package com.shepherd.app.data.dto.med_list.create_medlist
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita kohli on 01/08/22
 */
@Parcelize
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
):Parcelable