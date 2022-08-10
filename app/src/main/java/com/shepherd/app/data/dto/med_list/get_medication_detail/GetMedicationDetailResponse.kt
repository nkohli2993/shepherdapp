package com.shepherd.app.data.dto.med_list.get_medication_detail

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class GetMedicationDetailResponse(
    @SerializedName("payload") val payload: Payload
) : BaseResponseModel()