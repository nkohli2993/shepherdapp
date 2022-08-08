package com.shepherd.app.network.retrofit

import com.google.gson.annotations.SerializedName
import com.shepherd.app.ui.base.BaseResponseModel

/**
 * Created by Nikita Kohli on 08/08/2022
 */

data class DeleteAddedMedicationResponseModel(
    @SerializedName("payload") val payload: Boolean = false
) : BaseResponseModel()
