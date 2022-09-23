package com.shepherdapp.app.data.dto.chat

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Deepak Rattan on 18/08/22
 */

@Parcelize
data class MessageData(
    @SerializedName("content")
    var content: String? = "",
    @ServerTimestamp() var created: Timestamp? = null,

    @SerializedName("id")
    var id: String? = "",
    @SerializedName("message_type")
    var messageType: Int? = null,
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("attachment")
    var attachment: String? = null,
    @SerializedName("readIds")
    var readIds: ArrayList<String> = ArrayList(),
    @SerializedName("isRead")
    var isRead: Boolean? = false,
    @SerializedName("senderID")
    var senderID: String? = "",
    @SerializedName("senderName")
    var senderName: String? = "",
    var senderProfilePic: String? = null
) : Parcelable


@Parcelize
data class MessageGroupData(
    @SerializedName("date")
    var date: String? = null,
    @SerializedName("messages")
    var messageList: ArrayList<MessageData> = ArrayList()
) : Parcelable

@Parcelize
data class MessageGroupResponse(
    @SerializedName("scrollToBottom")
    var scrollToBottom: Boolean? = false,
    @SerializedName("msg_group")
    var groupList: ArrayList<MessageGroupData> = ArrayList()
) : Parcelable

@Parcelize
data class ChatNotificationModel(
    @SerializedName("data")
    var data: ChatNotificationData? = null,
    @SerializedName("user_id")
    var userId: String? = null,
    @SerializedName("chat_type")
    var chatType: Int? = null,
    @SerializedName("group_id")
    var groupId: String? = null,
    @SerializedName("notification")
    var notification: ChatNotificationData?,
    @SerializedName("to")
    var to: String? = null,
    @SerializedName("registration_ids")
    var registrationIDs: ArrayList<String>? = null
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ChatNotificationData(
    @SerializedName("body")
    var body: String? = null,
    @SerializedName("chat_id")
    var chatId: String? = null,
    @SerializedName("sound")
    var sound: String? = null,
    @SerializedName("user_id", alternate = ["sender_id"])
    var senderId: String? = null,
    @SerializedName("to_id")
    var toId: String? = null,
    @SerializedName("image-url")
    var imageUrl: String? = null,
    @SerializedName("id")
    var id: String? = null,
//    @ServerTimestamp var created_at: Timestamp? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("to_name")
    var toName: String? = null,
    @SerializedName("from_image")
    var fromImage: String? = null,
    @SerializedName("from_name", alternate = ["sender_name"])
    var senderName: String? = null,
    @SerializedName("title")
    var title: String? = null
) : Parcelable