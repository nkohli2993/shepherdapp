package com.shepherd.app.data.dto.chat

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 18/08/22
 */

@Parcelize
data class ChatListData(
    @SerializedName("sender_id")
    var senderId: String? = "",
    @SerializedName("latest_message")
    var latestMessage: String? = "",
//    @SerializedName("updated_at")
//    var updatedAt: String?="",
    @SerializedName("userIDs")
    var userIDs: ArrayList<String>? = ArrayList(),
    @SerializedName("user_ids_leave_group")
    var userIDsLeaveGroup: ArrayList<String>? = ArrayList(),
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("chat_type")
    var chatType: Int? = null,
    @Transient
    var unreadCount: Int? = null,
    @SerializedName("last_message_type")
    var lastMessageType: Int? = null,
    @SerializedName("group_name")
    var groupName: String? = null,
    @SerializedName("group_image")
    var groupImage: String? = null,
    @SerializedName("users_data")
    var usersDataMap: HashMap<String, ChatUserDetail?> = HashMap(),

    @SerializedName("to_user")
    var toUser: ChatUserDetail? = null,
    @ServerTimestamp var updated_at: Timestamp? = null,

    @SerializedName("is_blocked")
    var isBlocked: Boolean? = null,

    @SerializedName("blockedBy")
    var blockedBy: String? = null,

    @SerializedName("blockedTo")
    var blockedTo: String? = null,

    @SerializedName("community_name")
    var community_name: String? = null,

    @SerializedName("community_id")
    var community_id: String? = null

) : Parcelable

@Parcelize
data class ChatUserDetail(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("imageUrl")
    var imageUrl: String = "",
    @SerializedName("unread_count", alternate = ["unreadCount"])
    var unreadCount: Int = 0,
    @SerializedName("name")
    var name: String = ""
) : Parcelable


@Parcelize
data class ChatListResponse(
    @SerializedName("list")
    var list: ArrayList<ChatListData?> = ArrayList(),
    @SerializedName("scroll_to_bottom")
    var scrollToBottom: Boolean = false
) : Parcelable