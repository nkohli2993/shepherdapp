package com.shepherd.app.data.dto.med_list

import com.google.gson.annotations.SerializedName
import com.shepherd.app.data.dto.med_list.schedule_medlist.ScheduledPayload
import com.shepherd.app.ui.base.BaseResponseModel


/* Created by Nikita kohli on 05/08/22
*/
data class AddScheduledMedicationResponseModel(
    @SerializedName("payload") var payload: ScheduledPayload? = ScheduledPayload()
) : BaseResponseModel()
