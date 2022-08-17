package com.shepherd.app.data.dto.chat

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 16/08/22
 */

@Parcelize
data class ChatModel(
    // Chat Id
    var chatId: String? = null,
    //Sender
    var senderID: Int? = null,
    var senderName: String? = null,
    //Receiver
    var receiverID: Int? = null,
    var receiverName: String? = null,
    var receiverPicUrl: String? = null,
    var message: String? = null,
    //Chat Type
    var chatType: Int? = null,
    @ServerTimestamp() var created: Timestamp? = null,
) : Parcelable

