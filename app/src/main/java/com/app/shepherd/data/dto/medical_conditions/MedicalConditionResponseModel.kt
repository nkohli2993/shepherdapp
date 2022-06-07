package com.app.shepherd.data.dto.medical_conditions

import com.app.shepherd.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 07/06/22
 */
data class MedicalConditionResponseModel(

    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
