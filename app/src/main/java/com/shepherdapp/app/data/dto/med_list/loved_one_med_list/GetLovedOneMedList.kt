package com.shepherdapp.app.data.dto.med_list.loved_one_med_list

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 04/08/22
 */
data class GetLovedOneMedList(
    @SerializedName("payload" ) var payload : Payload? = Payload()
//    @SerializedName("payload") var payload: ArrayList<Payload> = arrayListOf()
) : BaseResponseModel()
