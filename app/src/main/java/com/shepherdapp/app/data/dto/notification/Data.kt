package com.shepherdapp.app.data.dto.notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 20/09/22
 */
data class Data (

    @SerializedName("user_id"         ) var userId         : String?       = null,
    @SerializedName("notification_id" ) var notificationId : Int?          = null,
    @SerializedName("is_read"         ) var isRead         : Boolean?      = null,
    @SerializedName("notification"    ) var notification   : Notification? = Notification()
)
