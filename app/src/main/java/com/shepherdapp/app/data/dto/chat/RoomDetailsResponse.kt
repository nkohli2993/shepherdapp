package com.shepherdapp.app.data.dto.chat

import android.os.Parcelable
import com.shepherdapp.app.data.dto.DeleteChat
import kotlinx.android.parcel.Parcelize

@Parcelize
class RoomDetailsResponse//Add this
    (): Parcelable {
    val deletedChatUserIds: ArrayList<DeleteChat> = ArrayList()
    val lastMessage: String? = null
    val roomId: String = ""
    val unseenMessageCount: Long = 0L
    val user1: User? = null
    val user2: User? = null
    val users: ArrayList<Long> = ArrayList()


}
