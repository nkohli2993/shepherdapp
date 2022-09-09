package com.shepherd.app.data.dto.med_list.schedule_medlist
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DosePayload(
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("dosages")
    var dosages: ArrayList<DoseList> = arrayListOf(),
    @SerializedName("lock_box_types")
    var dosagesTypes: ArrayList<DoseList> = arrayListOf(),
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("total_pages")
    var totalPages: Int = 0
):Parcelable
