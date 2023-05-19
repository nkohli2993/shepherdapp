package com.shepherdapp.app.ui.component.chat.extensions

import android.app.Dialog
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.DeleteChat
import com.shepherdapp.app.ui.component.chat.MessageFragment
import java.util.ArrayList
import com.google.firebase.Timestamp
import java.util.Calendar

fun MessageFragment.showDeleteChatDialog(userId:Long, roomID:String) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_delete_chat)
    dialog.setCancelable(true)

    val btnNo = dialog.findViewById(R.id.btnNo) as AppCompatButton
    val btnYes = dialog.findViewById(R.id.btnYes) as AppCompatButton

    btnYes.setOnClickListener {
        val deleteChatUserIdListing: ArrayList<DeleteChat> = ArrayList()
        deleteChatUserIdListing.add(DeleteChat(userId,Calendar.getInstance().timeInMillis))
        messagesViewModel.deleteChat(roomID, deleteChatUserIdListing)
        updateRecyclerView()
        dialog.dismiss()
    }

    btnNo.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}
