package com.shepherdapp.app.data.dto.subscription.get_active_subscriptions

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 24/11/22
 */
data class GetActiveSubscriptionResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
