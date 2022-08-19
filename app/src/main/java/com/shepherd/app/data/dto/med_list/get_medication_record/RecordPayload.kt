package com.shepherd.app.data.dto.med_list.get_medication_record

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita Kohli  19/08/22
 */

data class RecordPayload (
    @SerializedName("total"        ) var total       : Int?                  = null,
    @SerializedName("current_page" ) var currentPage : Int?                  = null,
    @SerializedName("total_pages"  ) var totalPages  : Int?                  = null,
    @SerializedName("per_page"     ) var perPage     : Int?                  = null,
    @SerializedName("data"         ) val data : ArrayList<MedicationRecordData>,

)
