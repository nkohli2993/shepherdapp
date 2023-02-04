package com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 04/02/23
 */
data class GetPreviousSubscriptionsResponseModel(
    @SerializedName("payload" ) var payload : Payload? = Payload()
):BaseResponseModel()
