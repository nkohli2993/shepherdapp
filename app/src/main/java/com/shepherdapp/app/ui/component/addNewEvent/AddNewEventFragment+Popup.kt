package com.shepherdapp.app.ui.component.addNewEvent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.widget.RadioButton
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
import java.util.Date
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun AddNewEventFragment.showRepeatDialog(startDate: String, recurringValue: EventRecurringModel?) {
    val dialog = Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_repeat_event)
    dialog.setCancelable(true)

    val btnNo = dialog.findViewById(R.id.btnNo) as AppCompatButton
    val btnYes = dialog.findViewById(R.id.btnYes) as AppCompatButton
    val radioGroup = dialog.findViewById(R.id.repeatOptionRG) as RadioGroup
    val dayRB = dialog.findViewById(R.id.dayRB) as RadioButton
    val weekRB = dialog.findViewById(R.id.weekRB) as RadioButton
    val monthRB = dialog.findViewById(R.id.monthRB) as RadioButton
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
    calendarPView.setOnDateChangedListener { _, date, _ ->
        val calendar = Calendar.getInstance()
        calendar.time = date.date
    }

    var weekAry: ArrayList<WeekDataModel> = arrayListOf()
    val selectedDays: ArrayList<WeekDataModel> = arrayListOf()
    val addedWeekDays: ArrayList<WeekDataModel> = arrayListOf()


    val weekArray = resources.getStringArray(R.array.week_array)
    for (i in weekArray.indices) {
        weekAry.add(WeekDataModel((i + 1), weekArray[i]))
    }
    weekAry.add(WeekDataModel(weekAry.size, "Sun"))

    if (recurringValue != null && recurringValue.type == RecurringEvent.Weekly.value && recurringValue.value != null) {
        for (i in weekAry) {
            for (j in recurringValue.value!!) {
                i.isSelected = false
                if (i.id == j) {
                    selectedDays.add(i)
                    i.isSelected = true
                    break
                }
            }
            addedWeekDays.add(i)
        }
    }

    if (addedWeekDays.size > 0) weekAry = addedWeekDays

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


    if (recurringValue?.endDate != null) {
        val dateSelected =
            SimpleDateFormat("MM/dd/yyyy").parse(recurringValue.endDate!!)
        val endDate =
            dateSelected?.let { SimpleDateFormat("MM/dd/yyyy").format(it) }
        tvEndDate.text = endDate
    }


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

    if (recurringValue?.type != null) {
        dayRB.isChecked = false
        weekRB.isChecked = false
        monthRB.isChecked = false

        when (recurringValue.type) {
            RecurringEvent.Daily.value -> {
                value.type = RecurringEvent.Daily.value
                dayRB.isChecked = true
            }

            RecurringEvent.Weekly.value -> {
                value.type = RecurringEvent.Weekly.value
                weekRB.isChecked = true
                Handler(Looper.getMainLooper()).postDelayed({
                    calenderCL.isVisible = false
                    weekdaysRV.isVisible = true
                }, 100)
            }

            RecurringEvent.Monthly.value -> {
                value.type = RecurringEvent.Monthly.value
                monthRB.isChecked = true
                Handler(Looper.getMainLooper()).postDelayed({
                    calenderCL.isVisible = true
                    weekdaysRV.isVisible = false
                }, 100)
            }

            else -> {

            }
        }

    }

    if (recurringValue != null && recurringValue.type == RecurringEvent.Monthly.value && recurringValue.value != null) {
        val date = SimpleDateFormat("MM-yyyy").format(Calendar.getInstance().time)
        for (i in recurringValue.value!!) {
            val monthDate = i.toString().plus("-$date")
            val dateShow: Date = SimpleDateFormat("dd-MM-yyyy").parse(monthDate) as Date
            val calendar = Calendar.getInstance()
            calendar.time = dateShow
            calendarPView.setDateSelected(calendar, true)
        }
        calenderCL.isVisible = true
        weekdaysRV.isVisible = false
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

                    val date: ArrayList<Int> = arrayListOf()
                    val days: ArrayList<String> = arrayListOf()
                    for (i in selectedDays) {
                        date.add(i.id!!)
                        days.add(i.name!!)
                    }

                    val startDateValue = SimpleDateFormat("MM/dd/yyyy").parse(startDate)
                    val endDateValue =
                        SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())

                    val diff: Long = endDateValue!!.time - startDateValue!!.time
                    val day = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
                    val weekDays: ArrayList<String> = arrayListOf()
                    if (day <= 7) {
                        val cal = Calendar.getInstance()
                        for (i in 0 until day.toInt()) {
                            cal.add(Calendar.DATE, 1)
                            weekDays.add(SimpleDateFormat("EEE").format(cal.time))
                        }
                        var exist = false
                        for (i in days) {
                            for (j in weekDays) {
                                if (i == j) {
                                    exist = true
                                    break
                                }
                            }
                        }
                        if (exist) {
                            dialog.dismiss()
                            tvEndDate.text = ""
                            showEventEndDate(
                                EventRecurringModel(
                                    RecurringEvent.Weekly.value,
                                    date,
                                    tvEndDate.text.toString(), "week"
                                ), days.joinToString()
                            )

                        } else {
                            showError(
                                requireContext(),
                                getString(R.string.please_select_other_date_no_event_recurring_occurs_in_between_these_dates)
                            )
                        }
                    } else {
                        dialog.dismiss()
                        showEventEndDate(
                            EventRecurringModel(
                                RecurringEvent.Weekly.value,
                                date,
                                tvEndDate.text.toString(), "week"
                            ), days.joinToString()
                        )

                    }

                }
            }

            RecurringEvent.Monthly.value -> {
                val selectedDates = calendarPView.selectedDates
                if (selectedDates.isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_atleast_one_date))
                } else if (tvEndDate.text.toString().isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_end_date))
                } else {

                    val date: ArrayList<Int> = arrayListOf()
                    for (i in selectedDates) {
                        val currentDateCalendar = Calendar.getInstance()
                        currentDateCalendar.time = i.date
                        date.add(SimpleDateFormat("dd").format(currentDateCalendar.time).toInt())
                    }
                    date.sort()
                    val lastDate = SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())
                    val startDateDate = SimpleDateFormat("MM/dd/yyyy").parse(startDate)
                    val lastSelectedDate = SimpleDateFormat("dd").format(lastDate!!)
                    val lastSelectedMonth = SimpleDateFormat("MM").format(lastDate)
                    val startSelectedMonth = SimpleDateFormat("MM").format(startDateDate!!)
                    if (lastSelectedMonth == startSelectedMonth) {
                        if (lastSelectedDate.toInt() < date[0]) {
                            showError(
                                requireContext(),
                                getString(R.string.please_select_other_date_no_event_recurring_occurs_in_between_these_dates)
                            )
                        } else {
                            dialog.dismiss()
                            showEventEndDate(
                                EventRecurringModel(
                                    RecurringEvent.Monthly.value,
                                    date,
                                    tvEndDate.text.toString(), "month"
                                )
                            )

                        }
                    } else {
                        dialog.dismiss()
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

    }

    btnNo.setOnClickListener {
        if (recurringValue != null) {
            if (recurringValue.type == RecurringEvent.Weekly.value && recurringValue.value != null) {
                val days: ArrayList<String> = arrayListOf()
                for (i in selectedDays) {
                    days.add(i.name!!)
                }
                showEventEndDate(
                    recurringValue,days.joinToString()
                )
            } else {
                showEventEndDate(
                    recurringValue
                )
            }
        } else {
            showEventEndDate(
                EventRecurringModel(
                    RecurringEvent.None.value,
                    null,
                    null
                )
            )
        }

        dialog.dismiss()
    }

    dialog.show()
}

@SuppressLint("SetTextI18n", "SimpleDateFormat")
fun datePicker(tvEndDate: AppCompatTextView, startDate: String) {
    val c = Calendar.getInstance()
    if (tvEndDate.text.toString().isNotEmpty()) {
        val dateSelected =
            SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())
        c.time = dateSelected!!
    }
    val mYear = c[Calendar.YEAR]
    val mMonth = c[Calendar.MONTH]
    val mDay = c[Calendar.DAY_OF_MONTH]


    val minCalendar = Calendar.getInstance()
    minCalendar.time = SimpleDateFormat("MM/dd/yyyy").parse(startDate)!!
    minCalendar.add(Calendar.DATE, 1)

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
    datePickerDialog.datePicker.minDate = minCalendar.timeInMillis
    datePickerDialog.show()
}
