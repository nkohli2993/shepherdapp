package com.shepherdapp.app.data.dto.add_vital_stats

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.add_vital_stats.vital_stats_dashboard.Payload
import com.shepherdapp.app.ui.base.BaseResponseModel


/* Created by Nikita Kohli on 22/08/22
*/
data class VitalStatsResponseModel(
    @SerializedName("payload") var payload: Payload = Payload()
) : BaseResponseModel()
