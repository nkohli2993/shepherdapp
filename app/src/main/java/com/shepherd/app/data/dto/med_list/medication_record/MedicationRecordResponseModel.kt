package com.shepherd.app.data.dto.med_list.medication_record

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class MedicationRecordResponseModel(
    @SerializedName("payload") val payload: Payload
) : BaseResponseModel()