package com.shepherdapp.app.data.dto.med_list.get_medication_record

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli  19/08/22
 */
@Parcelize
data class RecordPayload (
    @SerializedName("total"        ) var total       : Int?                  = null,
    @SerializedName("current_page" ) var currentPage : Int?                  = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?                  = null,
    @SerializedName("per_page"     ) var perPage     : Int?                  = null,
    @SerializedName("data"         ) val data : ArrayList<MedicationRecordData>,

):Parcelable
