package com.shepherd.app.data.dto.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 16/08/22
 */

@Parcelize
data class ChatModel(
    //Sender
    var senderID: Int? = null,
    var senderName: String? = null,
    //Receiver
    var receiverID: Int? = null,
    var receiverName: String? = null,
    var receiverPicUrl: String? = null,
    var message: String? = null,
    //Chat Type
    var chatType: Int? = null
) : Parcelable

