package com.shepherdapp.app.data.dto.medical_conditions

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 07/06/22
 */
data class MedicalConditionResponseModel(

    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
