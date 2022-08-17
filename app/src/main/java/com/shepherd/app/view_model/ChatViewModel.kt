package com.shepherd.app.view_model

import com.google.firebase.firestore.FieldValue
import com.shepherd.app.ShepherdApp.Companion.db
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.chat.ChatModel
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.TableName
import com.shepherd.app.utils.serializeToMap
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class ChatViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {
    var chatModel: ChatModel? = null
    var isListenerInitialized: Boolean = false

    fun sendMessage(chatModel: ChatModel) {
        this.chatModel = chatModel
        if (isListenerInitialized) {
            addMessageInDb(chatModel)
        } else {
            createNewChat {
                if (it) {
                    addMessageInDb(chatModel)
                }
            }
        }
    }

    private fun addMessageInDb(chatModel: ChatModel) {
        val chatReference = db.collection(TableName.CHATS).document(chatModel.id ?: "")
        chatReference.collection(TableName.MESSAGES)
            .add(chatModel.serializeToMap()).addOnSuccessListener {
                chatReference.collection(TableName.MESSAGES).document(it.id).update(
                    hashMapOf(
                        "id" to it.id, "created" to FieldValue.serverTimestamp()
                    ) as Map<String, Any>
                )
            }
    }

    private fun createNewChat(onChatCreated: (created: Boolean) -> Unit) {
        if (chatModel?.id.isNullOrEmpty()) {
            db.collection(TableName.CHATS).add(chatModel.serializeToMap())
                .addOnSuccessListener {

                    db.collection(TableName.CHATS).document(it.id).update("id", it.id)
                    chatModel?.id = it.id
                    initChatListener()
                    onChatCreated(true)
                }
        } else {
            db.collection(TableName.CHATS).document(chatModel?.id ?: "")
                .set(chatModel.serializeToMap())
                .addOnSuccessListener {
                    initChatListener()
                    onChatCreated(true)
                }
        }

    }

    private fun initChatListener() {
        isListenerInitialized = true
    }


}
