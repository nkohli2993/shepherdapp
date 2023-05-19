package com.shepherdapp.app.ui.component.addNewEvent

import android.R
import android.app.Dialog
import android.view.Window
import androidx.appcompat.widget.AppCompatButton

fun AddNewEventFragment.showRepeatDialog() {
    val dialog = Dialog(requireContext(), R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(com.shepherdapp.app.R.layout.dialog_repeat_event)
    dialog.setCancelable(true)

    val btnNo = dialog.findViewById(com.shepherdapp.app.R.id.btnNo) as AppCompatButton
    val btnYes = dialog.findViewById(com.shepherdapp.app.R.id.btnYes) as AppCompatButton

    btnYes.setOnClickListener {

    }

    btnNo.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
}
