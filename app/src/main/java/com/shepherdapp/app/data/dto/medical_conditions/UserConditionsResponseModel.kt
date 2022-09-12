package com.shepherdapp.app.data.dto.medical_conditions

import com.shepherdapp.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 14/06/22
 */

data class UserConditionsResponseModel(
    @SerializedName("payload") var payload: ArrayList<PayloadMedicalConditions> = arrayListOf()
) : BaseResponseModel()
