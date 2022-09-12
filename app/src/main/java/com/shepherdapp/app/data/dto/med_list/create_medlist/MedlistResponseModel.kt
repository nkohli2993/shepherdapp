package com.shepherdapp.app.data.dto.med_list.create_medlist

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Nikita kohli on 01/08/22
 */
data class MedlistResponseModel(
    @SerializedName("payload") var payload: MedListPayload? = MedListPayload()
) : BaseResponseModel()
