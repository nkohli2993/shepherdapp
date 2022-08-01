package com.shepherd.app.data.dto.med_list.create_medlist

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Nikita kohli on 01/08/22
 */
data class MedlistResponseModel(
    @SerializedName("payload") var payload: MedListPayload? = MedListPayload()
) : BaseResponseModel()
