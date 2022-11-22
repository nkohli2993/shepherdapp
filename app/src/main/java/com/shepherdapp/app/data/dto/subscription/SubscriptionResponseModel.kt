package com.shepherdapp.app.data.dto.subscription

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 22/11/22
 */
data class SubscriptionResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
