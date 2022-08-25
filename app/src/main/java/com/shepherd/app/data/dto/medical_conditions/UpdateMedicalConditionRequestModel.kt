package com.shepherd.app.data.dto.medical_conditions

import com.google.gson.annotations.SerializedName

/**
 * Created by Nikita Kohli on 25/08/22
 */
data class UpdateMedicalConditionRequestModel(

    @SerializedName("addNew_ids") var addNew_ids:ArrayList<MedicalConditionsLovedOneRequestModel>,
    @SerializedName("delete_ids") var delete_ids: ArrayList<Int>
)
