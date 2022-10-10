package com.shepherdapp.app.data.dto.notification.read_notifications

import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ui.base.BaseResponseModel

/**
 * Created by Deepak Rattan on 10/10/22
 */
data class ReadNotificationsResponseModel(
    @SerializedName("payload") var payload: Boolean? = null
) : BaseResponseModel()