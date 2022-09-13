package com.shepherdapp.app.data.dto.medical_conditions

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita Rattan on 14/06/22
 */

data class AddedUserMedicalConditionResposneModel(
    @SerializedName("payload") var payload: PayloadMedicalConditions = PayloadMedicalConditions()
) : BaseResponseModel()
