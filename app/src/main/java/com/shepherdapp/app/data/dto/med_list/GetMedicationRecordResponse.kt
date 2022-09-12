package com.shepherdapp.app.data.dto.med_list

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.data.dto.med_list.get_medication_record.RecordPayload
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Nikita Kohli  19/08/22
 */
data class GetMedicationRecordResponse(
    @SerializedName("payload") val payload: RecordPayload
) : BaseResponseModel()