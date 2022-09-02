package com.shepherd.app.data.dto.resource
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Payload(
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("data")
    var `data`: ArrayList<AllResourceData> = arrayListOf(),
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("total_pages")
    var totalPages: Int = 0
):Parcelable
