package com.shepherdapp.app.ui.component.addNewEvent

import android.app.Dialog
import android.view.Window
import android.widget.CalendarView
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.utils.RecurringEvent
import com.shepherdapp.app.utils.extensions.showError
import java.util.Calendar

fun AddNewEventFragment.showRepeatDialog() {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_repeat_event)
    dialog.setCancelable(true)

    val btnNo = dialog.findViewById(R.id.btnNo) as AppCompatButton
    val btnYes = dialog.findViewById(R.id.btnYes) as AppCompatButton
    val radioGroup = dialog.findViewById(R.id.repeatOptionRG) as RadioGroup
    val tvEndDate = dialog.findViewById(R.id.tvEndDate) as AppCompatTextView
    val weekdaysRV = dialog.findViewById(R.id.weekdaysRV) as RecyclerView
    val calenderCL = dialog.findViewById(R.id.calenderCL) as CalendarView
    val calendarPView = dialog.findViewById(R.id.calendarPView) as MaterialCalendarView
    calendarPView.arrowColor = ContextCompat.getColor(requireContext(),R.color.transparent)
    calendarPView.isScrollContainer = false
    calendarPView.setOnDateChangedListener { widget, date, selected ->
        val calendar = Calendar.getInstance()
        calendar.time = date.date
    }

    val value = EventRecurringModel()
    radioGroup.setOnCheckedChangeListener { group, checkedId ->
        when(radioGroup.checkedRadioButtonId){
            R.id.noneRB ->{
                showEventEndDate(EventRecurringModel(RecurringEvent.None.value,"",""))
                dialog.dismiss()
            }
            R.id.dayRB ->{
                value.type = RecurringEvent.Daily.value
            }
            R.id.weekRB ->{
                value.type = RecurringEvent.Weekly.value
                weekdaysRV.isVisible = true
            }
            R.id.monthRB ->{
                value.type = RecurringEvent.Monthly.value
                calenderCL.isVisible = true
            }

        }
    }


    btnYes.setOnClickListener {
        when (value.type) {
            RecurringEvent.None.value -> {
                showEventEndDate(EventRecurringModel(RecurringEvent.None.value,"",""))
                dialog.dismiss()
            }

            RecurringEvent.Daily.value -> {
                if(tvEndDate.text.toString().isEmpty()){
                    showError(requireContext(),getString(R.string.please_select_end_date))
                }
                else{
                    showEventEndDate(EventRecurringModel(RecurringEvent.Daily.value,"",""))
                }
            }

            RecurringEvent.Weekly.value -> {
                if(tvEndDate.text.toString().isEmpty()){
                    showError(requireContext(),getString(R.string.please_select_end_date))
                }
                else{
                    showEventEndDate(EventRecurringModel(RecurringEvent.Weekly.value,"",""))
                }
            }

            RecurringEvent.Monthly.value -> {
                if(tvEndDate.text.toString().isEmpty()){
                    showError(requireContext(),getString(R.string.please_select_end_date))
                }
                else{
                    showEventEndDate(EventRecurringModel(RecurringEvent.Monthly.value,"",""))
                }
            }
        }

    }

    btnNo.setOnClickListener {
        showEventEndDate(EventRecurringModel(RecurringEvent.None.value,"",""))
        dialog.dismiss()
    }

    dialog.show()
}


