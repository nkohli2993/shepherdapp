package com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 12/08/22
 */
data class GetLovedOneMedicalConditionsResponseModel(
    @SerializedName("payload") var payload: ArrayList<Payload> = arrayListOf()
) : BaseResponseModel()
