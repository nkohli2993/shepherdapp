package com.shepherdapp.app.data.dto.push_notification

import com.google.gson.annotations.SerializedName

/**
 * Created by Deepak Rattan on 23/09/22
 */
data class Result(
    @SerializedName("message_id") var messageId: String? = null
)
