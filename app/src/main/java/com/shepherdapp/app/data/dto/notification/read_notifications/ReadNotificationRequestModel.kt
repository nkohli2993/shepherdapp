package com.shepherdapp.app.data.dto.notification.read_notifications

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 10/10/22
 */
data class ReadNotificationRequestModel(
    @SerializedName("notification_id") var notificationId: Int? = null
)
