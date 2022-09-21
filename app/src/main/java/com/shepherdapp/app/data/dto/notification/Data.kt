package com.shepherdapp.app.data.dto.notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 20/09/22
 */
data class Data (

    @SerializedName("id"              ) var id             : Int?          = null,
    @SerializedName("user_id"         ) var userId         : String?       = null,
    @SerializedName("notification_id" ) var notificationId : Int?          = null,
    @SerializedName("is_read"         ) var isRead         : Boolean?      = null,
    @SerializedName("created_at"      ) var createdAt      : String?       = null,
    @SerializedName("updated_at"      ) var updatedAt      : String?       = null,
    @SerializedName("deleted_at"      ) var deletedAt      : String?       = null,
    @SerializedName("notification"    ) var notification   : Notification? = Notification()

)
