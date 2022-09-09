package com.shepherd.app.data.dto.medical_conditions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 07/06/22
 */
@Parcelize
data class Payload (

    @SerializedName("total"        ) var total       : Int?                  = null,
    @SerializedName("current_page" ) var currentPage : Int?                  = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?                  = null,
    @SerializedName("per_page"     ) var perPage     : Int?                  = null,
    @SerializedName("conditions"   ) var conditions  : ArrayList<Conditions> = arrayListOf()

):Parcelable
