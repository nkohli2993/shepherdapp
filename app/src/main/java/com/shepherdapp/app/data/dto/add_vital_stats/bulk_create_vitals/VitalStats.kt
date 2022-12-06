package com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 06/12/22
 */
data class VitalStats (
    @SerializedName("data"            ) var data          : Data?   = Data(),
    @SerializedName("date"            ) var date          : String? = null,
    @SerializedName("time"            ) var time          : String? = null,
    @SerializedName("loveone_user_id" ) var loveoneUserId : String? = null

)
