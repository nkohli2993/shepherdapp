package com.shepherdapp.app.data.dto.subscription.validate_subscription

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 22/12/22
 */
data class ValidateSubscriptionResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
