package com.shepherdapp.app.data.dto.medical_conditions

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Nikita Kohli on 25/08/22
 */
@Parcelize
data class UpdateMedicalConditionRequestModel(

    @SerializedName("addNew_ids") var addNew_ids:ArrayList<MedicalConditionsLovedOneRequestModel>,
    @SerializedName("delete_ids") var delete_ids: ArrayList<Int>
):Parcelable
