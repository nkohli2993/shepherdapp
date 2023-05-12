package com.shepherdapp.app.data.dto.chat

import com.shepherdapp.app.data.dto.ChatUsers
import com.shepherdapp.app.data.dto.DeleteChat
import com.shepherdapp.app.data.dto.LastMessageUser

class ChatUserListing//Add this
    () : java.io.Serializable {
    val user_id_leave_group: ArrayList<DeleteChat>? = null
    val last_message: LastMessageUser? = null
    val room_id: String? = null
    val updated_at: String? = null
//    val unseenMessageCount: Long? = null
    val user1: ChatUsers? = null
    val user2: ChatUsers? = null
    val users: ArrayList<Long>? = null

    }