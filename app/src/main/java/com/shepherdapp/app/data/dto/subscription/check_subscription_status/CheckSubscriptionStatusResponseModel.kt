package com.shepherdapp.app.data.dto.subscription.check_subscription_status

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 22/11/22
 */
data class CheckSubscriptionStatusResponseModel(
    @SerializedName("payload") var payload: Payload? = Payload()
) : BaseResponseModel()
