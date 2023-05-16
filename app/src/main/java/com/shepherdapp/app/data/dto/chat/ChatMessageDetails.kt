package com.shepherdapp.app.data.dto.chat

import com.shepherdapp.app.data.dto.DeleteChat

data class ChatMessageDetails(
    var deletedChatUserIds: ArrayList<DeleteChat>,
    val lastMessages: String,
    val room_id: String,
    val user1: UserDataMessages,
    val user2: UserDataMessages,
    val users: ArrayList<Long>,
    val createdAt :Long
)