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
import com.shepherdapp.app.data.dto.MonthModel
import com.shepherdapp.app.data.dto.WeekDataModel
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.ui.component.addNewEvent.adapter.MonthAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.WeekAdapter
import com.shepherdapp.app.utils.RecurringEvent
import com.shepherdapp.app.utils.extensions.showError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

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
    val yearRB = dialog.findViewById(R.id.yearRB) as RadioButton
    val weekRB = dialog.findViewById(R.id.weekRB) as RadioButton
    val monthRB = dialog.findViewById(R.id.monthRB) as RadioButton
    val tvEndDate = dialog.findViewById(R.id.txtEndDate) as AppCompatTextView
    val weekdaysRV = dialog.findViewById(R.id.weekdaysRV) as RecyclerView
    val monthRV = dialog.findViewById(R.id.recyclerViewMonth) as RecyclerView
    val calenderCL = dialog.findViewById(R.id.calenderCL) as ConstraintLayout

    val calendarPView = dialog.findViewById(R.id.calendarPView) as MaterialCalendarView
    calendarPView.arrowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
    calendarPView.isScrollContainer = false
    calendarPView.selectionMode = MaterialCalendarView.SELECTION_MODE_SINGLE


    calendarPView.setOnDateChangedListener { _, date, _ ->
        val calendar = Calendar.getInstance()
        calendar.time = date.date
    }

    var weekAry: ArrayList<WeekDataModel> = arrayListOf()
    selectedDays.clear()
    val addedWeekDays: ArrayList<WeekDataModel> = arrayListOf()


    val weekArray = resources.getStringArray(R.array.week_array)
    for (i in weekArray.indices) {
        weekAry.add(WeekDataModel((i + 1), weekArray[i]))
    }
    weekAry.add(WeekDataModel(weekAry.size + 1, "Sun"))

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
    Handler(Looper.getMainLooper()).postDelayed({
        weekAdapter = WeekAdapter(weekAry, object : WeekAdapter.WeekDaySelected {
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
        weekdaysRV.adapter = weekAdapter
    }, 100)

    var monthArrayList: ArrayList<MonthModel> = ArrayList()
    for (i in 1..31) {
        val monthModel = MonthModel()
        monthModel.monthDate = i.toString()
        monthArrayList.add(monthModel)
    }

    // show selected month
    if (recurringValue != null && recurringValue.type == RecurringEvent.Monthly.value && recurringValue.value != null) {
        selectedMonthDates.clear()
        for (j in recurringValue.value!!) {
            val monthValue = MonthModel()
            monthValue.monthDate = j.toString()
            monthValue.isSelected = true
            selectedMonthDates.add(monthValue)
        }

        val selectedMonthArrayList: ArrayList<MonthModel> = ArrayList()
        for (i in monthArrayList) {
            for (j in selectedMonthDates) {
                i.isSelected = false
                if (i.monthDate == j.monthDate) {
                    i.isSelected = true
                    break
                }
            }
            selectedMonthArrayList.add(i)
        }
        if (selectedMonthArrayList.size > 0) {
            monthArrayList = selectedMonthArrayList
        }
    } else {
        selectedMonthDates.clear()
    }
    Handler(Looper.getMainLooper()).postDelayed({
        monthAdapter =
            MonthAdapter(dialog.context, monthArrayList, object : MonthAdapter.selectedMonth {
                override fun onMonthSelected(
                    monthModel: MonthModel,
                    position: Int,
                    monthList: ArrayList<MonthModel>
                ) {
                    selectedMonthDates.clear()
                    monthList.forEach {
                        if (it.isSelected) {
                            selectedMonthDates.add(it)
                        }
                    }
                }
            })

        monthRV.adapter = monthAdapter

    }, 100)

    if (recurringValue?.endDate != null) {
        val dateSelected =
            formate.parse(recurringValue.endDate!!)
        val endDate =
            dateSelected?.let { formate.format(it) }
        tvEndDate.text = endDate
    }


    tvEndDate.setOnClickListener {
        datePicker(tvEndDate, startDate,calendarPView)
    }

    val value = EventRecurringModel()
    radioGroup.setOnCheckedChangeListener { _, _ ->
        when (radioGroup.checkedRadioButtonId) {
            R.id.dayRB -> {
                clearSelectedDataAll(R.id.dayRB)
                if (tvEndDate.text.toString().isEmpty()) {
                    showEndDatePopup(radioGroup)
                } else {
                    value.type = RecurringEvent.Daily.value
                    showView(monthRV, calenderCL, weekdaysRV,
                        monthValue = false,
                        calendarValue = false,
                        weekValue = false
                    )
                }
                radioGroupCheckId = R.id.dayRB
            }

            R.id.weekRB -> {
                clearSelectedDataAll(R.id.weekRB)
                if (tvEndDate.text.toString().isEmpty()) {
                    showEndDatePopup(radioGroup)
                } else {
                    value.type = RecurringEvent.Weekly.value
                    showView(monthRV, calenderCL, weekdaysRV,
                        monthValue = false,
                        calendarValue = false,
                        weekValue = true
                    )
                }
                radioGroupCheckId = R.id.weekRB
            }

            R.id.monthRB -> {
                clearSelectedDataAll(R.id.monthRB)
                if (tvEndDate.text.toString().isEmpty()) {
                    showEndDatePopup(radioGroup)
                } else {
                    val startDateDate = formate.parse(startDate)
                    val lastDate = formate.parse(tvEndDate.text.toString())
                    monthAdapter.startEndDate(startDateDate!!, lastDate!!)
                    value.type = RecurringEvent.Monthly.value
                    showView(monthRV, calenderCL, weekdaysRV,
                        monthValue = true,
                        calendarValue = false,
                        weekValue = false
                    )
                }
                radioGroupCheckId = R.id.monthRB
            }

            R.id.yearRB -> {
                clearSelectedDataAll(R.id.yearRB)
                if (tvEndDate.text.toString().isEmpty()) {
                    showEndDatePopup(radioGroup)
                } else {
                    value.type = RecurringEvent.Yearly.value
                    showView(monthRV, calenderCL, weekdaysRV,
                        monthValue = false,
                        calendarValue = true,
                        weekValue = false
                    )
                }
                radioGroupCheckId = R.id.yearRB
            }
        }
    }

    if (recurringValue?.type != null) {
        dayRB.isChecked = false
        weekRB.isChecked = false
        monthRB.isChecked = false
        yearRB.isChecked = false

        when (recurringValue.type) {
            RecurringEvent.Daily.value -> {
                value.type = RecurringEvent.Daily.value
                dayRB.isChecked = true
                showView(monthRV, calenderCL, weekdaysRV,
                    monthValue = false,
                    calendarValue = false,
                    weekValue = false
                )
            }

            RecurringEvent.Weekly.value -> {
                value.type = RecurringEvent.Weekly.value
                weekRB.isChecked = true
                showView(monthRV, calenderCL, weekdaysRV,
                    monthValue = false,
                    calendarValue = false,
                    weekValue = true
                )
            }

            RecurringEvent.Monthly.value -> {
                value.type = RecurringEvent.Monthly.value
                monthRB.isChecked = true
                showView(monthRV, calenderCL, weekdaysRV,
                    monthValue = true,
                    calendarValue = false,
                    weekValue = false
                )
            }

            RecurringEvent.Yearly.value -> {
                value.type = RecurringEvent.Monthly.value
                yearRB.isChecked = true
                showView(monthRV, calenderCL, weekdaysRV,
                    monthValue = false,
                    calendarValue = true,
                    weekValue = false
                )
            }

            else -> {

            }
        }

    }

//    show yearly data
    if (recurringValue != null && recurringValue.type == RecurringEvent.Yearly.value && recurringValue.value_year != null) {
        val year = SimpleDateFormat("yyyy").format(Calendar.getInstance().time)
        for (i in recurringValue.value_year!!) {
            val monthDate = i.plus("-$year")
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
                dialog.dismiss()
                showEventEndDate(
                    EventRecurringModel(
                        RecurringEvent.Daily.value, null, tvEndDate.text.toString(), "day"
                    )
                )

            }

            RecurringEvent.Weekly.value -> {
                if (selectedDays.size <= 0) {
                    showError(requireContext(), getString(R.string.please_atleast_one_weekday))
                } else {

                    val date: ArrayList<Int> = arrayListOf()
                    val days: ArrayList<String> = arrayListOf()
                    for (i in selectedDays) {
                        date.add(i.id!!)
                        days.add(i.name!!)
                    }
                    date.sort()
                    showEventEndDate(
                        EventRecurringModel(
                            RecurringEvent.Weekly.value,
                            date,
                            tvEndDate.text.toString(), "week"
                        ), days.joinToString()
                    )
                    dialog.dismiss()
                }
            }

            RecurringEvent.Monthly.value -> {
                if (selectedMonthDates.isEmpty()) {
                    showError(requireContext(), getString(R.string.please_select_atleast_one_date))
                } else {

                    val date: ArrayList<Int> = arrayListOf()
                    for (i in selectedMonthDates) {
                        val currentDateCalendar = Calendar.getInstance()
                        currentDateCalendar.set(Calendar.DAY_OF_MONTH, i.monthDate.toInt())
                        date.add(SimpleDateFormat("dd").format(currentDateCalendar.time).toInt())
                    }
                    date.sort()
                    val lastDate = formate.parse(tvEndDate.text.toString())
                    val startDateDate = formate.parse(startDate)
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


            RecurringEvent.Yearly.value -> {
                val selectedDates = calendarPView.selectedDates
                if (selectedDates.size <= 0) {
                    showError(requireContext(), getString(R.string.please_atleast_one_weekday))
                } else {

                    val selected = formate.format(selectedDates[0].date)
                    val selectedDate = formate.parse(selected)

                    val endDateDate = formate.parse(tvEndDate.text.toString())
                    val startDateEvent = formate.parse(startDate)
                    if(selectedDate!!.before(startDateEvent)){
                        showError(requireContext(),getString(R.string.please_select_date_before_start_date))
                    } else if(endDateDate!!.before(selectedDate)){
                        showError(requireContext(),getString(R.string.please_select_date_before_end_date))
                    }
                    else{
                        val dates :ArrayList<String> = arrayListOf()
                        for(i in selectedDates){
                            dates.add(SimpleDateFormat("dd-MM").format(i.date))
                        }
                        showEventEndDate(
                            EventRecurringModel(
                                RecurringEvent.Yearly.value,
                                null,
                                tvEndDate.text.toString(), "year",dates), SimpleDateFormat("dd MMM").format(selectedDates[0].date)
                        )
                        dialog.dismiss()
                    }
                }
            }

        }
    }

    btnNo.setOnClickListener {
        if (recurringValue != null) {
            if (recurringValue.type == RecurringEvent.Weekly.value && recurringValue.value != null) {
                val days: ArrayList<String> = arrayListOf()
                selectedDays.clear()
                for (i in weekAry) {
                    for (j in recurringValue.value!!) {
                        if (i.id == j) {
                            selectedDays.add(i)
                            break
                        }
                    }
                }
                for (i in selectedDays) {
                    days.add(i.name!!)
                }
                showEventEndDate(
                    recurringValue, days.joinToString()
                )
            } else if (recurringValue.type == RecurringEvent.Yearly.value && recurringValue.value != null) {
                val calendarYear  = SimpleDateFormat("yyyy").format(Calendar.getInstance().time)
                val date = SimpleDateFormat("dd-MM-yyyy").parse(recurringValue.typeValue!![0].plus("-$calendarYear"))
                showEventEndDate(
                    recurringValue,  SimpleDateFormat("dd MMM").format(date!!)
                )
            }
            else {
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
            radioGroup.clearCheck()
        }


        dialog.dismiss()
    }

    dialog.show()
}

private fun showView(
    monthRV: RecyclerView,
    calenderCL: ConstraintLayout,
    weekdaysRV: RecyclerView, monthValue: Boolean, calendarValue: Boolean, weekValue: Boolean
) {
    Handler(Looper.getMainLooper()).postDelayed({
        monthRV.isVisible = monthValue
        calenderCL.isVisible = calendarValue
        weekdaysRV.isVisible = weekValue
    }, 100)
}

private fun AddNewEventFragment.showEndDatePopup(radioGroup: RadioGroup) {
    showToast(getString(R.string.please_select_end_date_proceed))
    radioGroup.clearCheck()
}

private fun AddNewEventFragment.clearSelectedDataAll(id: Int) {
    if (radioGroupCheckId != id) {
        selectedMonthDates.clear()
        monthAdapter.clearSelectedList()

        selectedDays.clear()
        weekAdapter.clearSelectedList()

    }

}

@SuppressLint("SetTextI18n", "SimpleDateFormat", "NotifyDataSetChanged")
fun AddNewEventFragment.datePicker(tvEndDate: AppCompatTextView, startDate: String, calendarPView: MaterialCalendarView) {
    val c = Calendar.getInstance()
    if (tvEndDate.text.toString().isNotEmpty()) {
        val dateSelected =
            formate.parse(tvEndDate.text.toString())
        c.time = dateSelected!!
    }
    val mYear = c[Calendar.YEAR]
    val mMonth = c[Calendar.MONTH]
    val mDay = c[Calendar.DAY_OF_MONTH]


    val minCalendar = Calendar.getInstance()
    minCalendar.time = formate.parse(startDate)!!
    minCalendar.add(Calendar.DATE, 1)

    val datePickerDialog = DatePickerDialog(
        tvEndDate.context, R.style.datepicker, { _, year, monthOfYear, dayOfMonth ->

            if (selectedDatePickerDate != "${
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
            ) {

                selectedMonthDates.clear()
                monthAdapter.clearSelectedList()

                selectedDays.clear()
                weekAdapter.clearSelectedList()

                calendarPView.clearSelection()
            }

            selectedDatePickerDate = "${
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

            tvEndDate.text = selectedDatePickerDate

        }, mYear, mMonth, mDay
    )
    datePickerDialog.datePicker.minDate = minCalendar.timeInMillis
    datePickerDialog.show()
}
