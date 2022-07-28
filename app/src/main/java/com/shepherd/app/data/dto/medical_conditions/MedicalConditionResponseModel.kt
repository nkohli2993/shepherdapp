package com.shepherd.app.data.dto.medical_conditions

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 07/06/22
 */
data class MedicalConditionResponseModel(

    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
