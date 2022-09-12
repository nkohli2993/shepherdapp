package com.shepherdapp.app.ui.component.addNewEvent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePicker : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mCalendar = Calendar.getInstance()
        val year = mCalendar[Calendar.YEAR]
        val month = mCalendar[Calendar.MONTH]
        val dayOfMonth = mCalendar[Calendar.DAY_OF_MONTH]
        val dialog = DatePickerDialog(requireActivity(), activity as DatePickerDialog.OnDateSetListener, year, month, dayOfMonth)
        dialog.datePicker.minDate = mCalendar.timeInMillis
        return dialog
    }
}