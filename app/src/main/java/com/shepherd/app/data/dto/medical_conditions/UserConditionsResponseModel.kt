package com.shepherd.app.data.dto.medical_conditions

import com.shepherd.app.ui.base.BaseResponseModel
import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 14/06/22
 */

data class UserConditionsResponseModel(
    @SerializedName("payload") var payload: ArrayList<PayloadMedicalConditions> = arrayListOf()
) : BaseResponseModel()
