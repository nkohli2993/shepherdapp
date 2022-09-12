package com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 12/08/22
 */
data class GetLovedOneMedicalConditionsResponseModel(
    @SerializedName("payload") var payload: ArrayList<Payload> = arrayListOf()
) : BaseResponseModel()
