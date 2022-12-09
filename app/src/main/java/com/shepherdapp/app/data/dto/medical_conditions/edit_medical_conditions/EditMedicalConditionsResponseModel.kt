package com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 09/12/22
 */
data class EditMedicalConditionsResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
