package com.shepherdapp.app.data.dto.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.shepherdapp.app.data.dto.ChatUsers
import com.shepherdapp.app.data.dto.DeleteChat

class ChatUserListing : java.io.Serializable {
    val deletedChatUserIds: ArrayList<DeleteChat> = arrayListOf()
    val lastMessages: String? = null
    val room_id: String? = null
    val unseenMessageCount: Long? = null
    @ServerTimestamp val createdAt: Timestamp? = null
    val user1: ChatUsers? = null
    val user2: ChatUsers? = null
    val users: ArrayList<Long>? = null
    val lastSenderId :Long? = null
}
