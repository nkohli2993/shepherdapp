package com.shepherd.app.data.dto.add_vital_stats
import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.Payload
import com.shepherd.app.ui.base.BaseResponseModel


/* Created by Nikita kohli on 22/08/22
*/

data class AddVitalStatsResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
