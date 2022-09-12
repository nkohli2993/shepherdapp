package com.shepherdapp.app.data.dto.med_list.medication_record

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 09/08/22
 */

data class MedicationRecordResponseModel(
    @SerializedName("payload") val payload: Payload
) : BaseResponseModel()