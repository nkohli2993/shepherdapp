package com.shepherdapp.app.ui.component.addNewEvent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.WeekDataModel
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.ui.component.carePoints.adapter.WeekAdapter
import com.shepherdapp.app.utils.RecurringEvent
import com.shepherdapp.app.utils.extensions.showError
import java.text.SimpleDateFormat
import java.util.Calendar

@SuppressLint("SimpleDateFormat")
fun AddNewEventFragment.showRepeatDialog(startDate: String) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_repeat_event)
    dialog.setCancelable(true)

    val btnNo = dialog.findViewById(R.id.btnNo) as AppCompatButton
    val btnYes = dialog.findViewById(R.id.btnYes) as AppCompatButton
    val radioGroup = dialog.findViewById(R.id.repeatOptionRG) as RadioGroup
    val tvEndDate = dialog.findViewById(R.id.txtEndDate) as AppCompatTextView
    val leftV = dialog.findViewById(R.id.leftV) as AppCompatTextView
    val rightV = dialog.findViewById(R.id.rightV) as AppCompatTextView
    val weekdaysRV = dialog.findViewById(R.id.weekdaysRV) as RecyclerView
    val calenderCL = dialog.findViewById(R.id.calenderCL) as ConstraintLayout
    val calendarPView = dialog.findViewById(R.id.calendarPView) as MaterialCalendarView
    calendarPView.arrowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
    calendarPView.isScrollContainer = false
    calendarPView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
    calendarPView.isPagingEnabled = false
    calendarPView.setOnDateChangedListener { widget, date, selected ->
        val calendar = Calendar.getInstance()
        calendar.time = date.date
    }

    val weekAry: ArrayList<WeekDataModel> = arrayListOf()
    val weekArray = resources.getStringArray(R.array.week_array)
    for (i in weekArray.indices) {
        weekAry.add(WeekDataModel((i + 1), weekArray[i]))
    }
    weekAry.add(WeekDataModel(weekAry.size,"Sun"))
    val selectedDays: ArrayList<WeekDataModel> = arrayListOf()
    val adapter = WeekAdapter(weekAry, object : WeekAdapter.WeekDaySelected {
        override fun onDaySelected(detail: ArrayList<WeekDataModel>) {
            selectedDays.clear()
            for (i in detail) {
                if (i.isSelected) {
                    selectedDays.add(i)
                }
            }
            selectedDays.sortBy { it.id }
        }
    })
    weekdaysRV.adapter = adapter

    tvEndDate.setOnClickListener {
        datePicker(tvEndDate, startDate)
    }
    leftV.setOnClickListener {

    }
    rightV.setOnClickListener {

    }
    val value = EventRecurringModel()
    radioGroup.setOnCheckedChangeListener { _, _ ->
        when (radioGroup.checkedRadioButtonId) {
            R.id.noneRB -> {
                dialog.dismiss()
                showEventEndDate(
                    EventRecurringModel(
                        RecurringEvent.None.value,
                        null,
                        null
                    )
                )

            }

            R.id.dayRB -> {
                value.type = RecurringEvent.Daily.value
                weekdaysRV.isVisible = false
                calenderCL.isVisible = false
            }

            R.id.weekRB -> {
                value.type = RecurringEvent.Weekly.value
                Handler(Looper.getMainLooper()).postDelayed({
                    calenderCL.isVisible = false
                    weekdaysRV.isVisible = true
                }, 100)
            }

            R.id.monthRB -> {
                value.type = RecurringEvent.Monthly.value
                Handler(Looper.getMainLooper()).postDelayed({
                    calenderCL.isVisible = true
                    weekdaysRV.isVisible = false
                }, 100)

            }

        }
    }

    btnYes.setOnClickListener {
        when (value.type) {
            RecurringEvent.None.value -> {
                dialog.dismiss()
                showEventEndDate(
                    EventRecurringModel(
                        RecurringEvent.None.value,
                        null,
                        null
                    )
                )

            }

            RecurringEvent.Daily.value -> {
                if (tvEndDate.text.toString().isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_end_date))
                } else {
                    dialog.dismiss()
                    showEventEndDate(
                        EventRecurringModel(
                            RecurringEvent.Daily.value, null, tvEndDate.text.toString(), "day"
                        )
                    )
                }
            }

            RecurringEvent.Weekly.value -> {
                if (selectedDays.size <= 0) {
                    showError(requireContext(), getString(R.string.please_atleast_one_weekday))
                } else if (tvEndDate.text.toString().isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_end_date))
                } else {
                    dialog.dismiss()
                    val date: ArrayList<Int> = arrayListOf()
                    val days: ArrayList<String> = arrayListOf()
                    for (i in selectedDays) {
                        date.add(i.id!!)
                        days.add(i.name!!)
                    }
                    showEventEndDate(
                        EventRecurringModel(
                            RecurringEvent.Weekly.value,
                            date,
                            tvEndDate.text.toString(), "week"
                        ), days.joinToString()
                    )

                }
            }

            RecurringEvent.Monthly.value -> {
                val selectedDates = calendarPView.selectedDates
                if (selectedDates.isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_atleast_one_date))
                } else if (tvEndDate.text.toString().isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_end_date))
                } else {
                    dialog.dismiss()
                    val date: ArrayList<Int> = arrayListOf()
                    for (i in selectedDates) {
                        val currentDateCalendar = Calendar.getInstance()
                        currentDateCalendar.time = i.date
                        date.add(SimpleDateFormat("dd").format(currentDateCalendar.time).toInt())
                    }
                    showEventEndDate(
                        EventRecurringModel(
                            RecurringEvent.Monthly.value,
                            date,
                            tvEndDate.text.toString(), "month"
                        )
                    )

                }
            }
        }

    }

    btnNo.setOnClickListener {
        showEventEndDate(
            EventRecurringModel(
                RecurringEvent.None.value,
                null,
                null
            )
        )

        dialog.dismiss()
    }

    dialog.show()
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun datePicker(tvEndDate: AppCompatTextView, startDate: String) {
    val c = Calendar.getInstance()
    val mYear = c[Calendar.YEAR]
    val mMonth = c[Calendar.MONTH]
    val mDay = c[Calendar.DAY_OF_MONTH]

    c.time = SimpleDateFormat("MM/dd/yyyy").parse(startDate)!!


    val datePickerDialog = DatePickerDialog(
        tvEndDate.context, R.style.datepicker, { _, year, monthOfYear, dayOfMonth ->
            tvEndDate.text = "${
                if (monthOfYear + 1 < 10) {
                    "0${(monthOfYear + 1)}"
                } else {
                    (monthOfYear + 1)
                }
            }/${
                if (dayOfMonth + 1 < 10) {
                    "0$dayOfMonth"
                } else {
                    dayOfMonth
                }
            }/$year"
        }, mYear, mMonth, mDay
    )
    datePickerDialog.datePicker.minDate = c.timeInMillis
    datePickerDialog.show()
}
