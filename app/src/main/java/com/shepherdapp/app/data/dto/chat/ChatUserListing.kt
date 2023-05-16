package com.shepherdapp.app.data.dto.chat

import com.shepherdapp.app.data.dto.ChatUsers
import com.shepherdapp.app.data.dto.DeleteChat

class ChatUserListing : java.io.Serializable {
    val deletedChatUserIds: ArrayList<DeleteChat>? = null
    val lastMessages: String? = null
    val room_id: String? = null
    val unseenMessageCount: Long? = null
    val user1: ChatUsers? = null
    val user2: ChatUsers? = null
    val users: ArrayList<Long>? = null
    val createdAt :Long? =null

    fun manageCreatedAt(): Long {
        return if (createdAt.toString().length == 10)
            createdAt!! * 1000
        else
            createdAt!!
    }

}
