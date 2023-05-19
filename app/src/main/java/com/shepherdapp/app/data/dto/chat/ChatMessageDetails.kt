package com.shepherdapp.app.data.dto.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import com.shepherdapp.app.data.dto.DeleteChat

data class ChatMessageDetails(
    var deletedChatUserIds: ArrayList<DeleteChat>,
    val lastMessages: String,
    val room_id: String,
    val user1: UserDataMessages,
    val user2: UserDataMessages,
    val users: ArrayList<Long>,
    @ServerTimestamp val createdAt: Timestamp,
    val unseenMessageCount:Long,
    val lastSenderId:String? = null
)