package com.shepherd.app.data.dto.med_list

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 01/08/22
 */
data class GetAllMedListResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
