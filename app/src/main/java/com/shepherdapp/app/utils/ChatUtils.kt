package com.shepherdapp.app.utils

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.data.dto.chat.MessageData
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.utils.extensions.createDate
import com.shepherdapp.app.utils.extensions.getStringDate
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Deepak Rattan on 16/08/22
 */

object TableName {
    const val CHATS = "chats"
    const val CHATS_DEV = "chatsDev"
    const val MESSAGES = "messages"
    const val USERS = "users"
    const val USERS_DEV = "usersDev"

    const val CARE_TEAM_CHATS = "careTeamChats"
    const val CARE_TEAM_CHATS_DEV = "careTeamChatsDev"
}

object Chat {
    const val CHAT_SINGLE = 1
    const val CHAT_GROUP = 2

    const val MESSAGE_TEXT = 1
    const val MESSAGE_IMAGE = 2
    const val MESSAGE_VIDEO = 3
}

fun <T> T.serializeToMap(): Map<String, Any?> {
    return convert()
}

//convert an object of type I to type O
inline fun <I, reified O> I.convert(): O {

    val json = Gson().toJson(this)
    return Gson().fromJson(json, object : TypeToken<O>() {}.type)
}

fun showLog(tag: String?, message: String?) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, message ?: "")
    }
}


@Throws(JSONException::class)
fun QueryDocumentSnapshot.getMessageModelFromDoc(): MessageData {
    val messageModel = Gson().fromJson(
        JSONObject(data).toString(),
        MessageData::class.java
    )

    messageModel.created =
        (data["created"] as Timestamp?)

    if (messageModel.id.isNullOrBlank()) {
        messageModel.id = id
    }
    return messageModel
}

fun ArrayList<MessageData>.sortMessages(): ArrayList<MessageGroupData> {
    distinctBy { it.id }
    val groupList =
        groupBy {
            val cal = Calendar.getInstance().apply {
                if (it.created != null) {
                    time = it.created!!.toDate()
                }
            }

            it.date = cal.time.getStringDate("yyyy-MM-dd HH:mm:ss")

            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${
                cal.get(
                    Calendar.DATE
                )
            }"
        }.map {
            val msgList = it.value as ArrayList<MessageData>
            msgList.sortBy { it.date?.createDate("yyyy-MM-dd HH:mm:ss") }
            MessageGroupData(
                it.key,
                msgList
            )
        } as ArrayList<MessageGroupData>




    groupList.sortByDescending { it.date?.createDate("yyyy-MM-dd")?.time }
    return groupList
}

fun showException(e: Exception) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
}