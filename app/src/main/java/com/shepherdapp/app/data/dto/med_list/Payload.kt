package com.shepherdapp.app.data.dto.med_list

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 01/08/22
 */
@Parcelize
data class Payload(
    @SerializedName("total"        ) var total       : Int?                = null,
    @SerializedName("current_page" ) var currentPage : Int?                = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?                = null,
    @SerializedName("per_page"     ) var perPage     : Int?                = null,
    @SerializedName("medlists"     ) var medlists    : ArrayList<Medlist> = arrayListOf()
):Parcelable
