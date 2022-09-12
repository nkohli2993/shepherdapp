package com.shepherdapp.app.data.dto.chat

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 16/08/22
 */

@Parcelize
data class ChatModel(
    // id is the document id
    var id: String? = null,
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
    var groupName: String? = null,
    var eventId: Int? = null,

    @ServerTimestamp()
    var created: Timestamp? = null,
) : Parcelable

