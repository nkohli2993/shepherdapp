package com.shepherdapp.app.data.dto.med_list

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.DosePayload
import com.shepherdapp.app.ui.base.BaseResponseModel

/* Created by Nikita kohli on 04/08/22
*/
data class GetAllDoseListResponseModel(
    @SerializedName("payload") var payload: DosePayload? = DosePayload()
) : BaseResponseModel()
