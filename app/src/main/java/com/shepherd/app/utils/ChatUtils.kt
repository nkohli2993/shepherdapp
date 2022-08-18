package com.shepherd.app.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Deepak Rattan on 16/08/22
 */

object TableName {
    const val CHATS = "chats"
    const val MESSAGES = "messages"
    const val USERS = "users"
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