package com.shepherdapp.app.ui.component.edit_care_point

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
import com.shepherdapp.app.data.dto.added_events.AddedEventModel
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.ui.component.addNewEvent.adapter.MonthAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.WeekAdapter
import com.shepherdapp.app.utils.RecurringEvent
import com.shepherdapp.app.utils.RecurringFlag
import com.shepherdapp.app.utils.extensions.showError
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun EditCarePointFragment.showRepeatDialog(carePoint: AddedEventModel) {
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
    val monthRV = dialog.findViewById(R.id.recyclerViewMonth) as RecyclerView
    val calenderCL = dialog.findViewById(R.id.calenderCL) as ConstraintLayout
    val calendarPView = dialog.findViewById(R.id.calendarPView) as MaterialCalendarView
    calendarPView.arrowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
    calendarPView.isScrollContainer = false
    calendarPView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
    calendarPView.isPagingEnabled = false

    val dayRB = dialog.findViewById(R.id.dayRB) as RadioButton
    val weekRB = dialog.findViewById(R.id.weekRB) as RadioButton
    val monthRB = dialog.findViewById(R.id.monthRB) as RadioButton


    val addedWeekDays: ArrayList<WeekDataModel> = arrayListOf()
    selectedWeekDays = arrayListOf()
    selectedMonthDates = arrayListOf()
    val value = EventRecurringModel()
    var weekAry: ArrayList<WeekDataModel> = arrayListOf()
    val weekArray = resources.getStringArray(R.array.week_array)


    calendarPView.arrowColor = ContextCompat.getColor(requireContext(), R.color.transparent)
    calendarPView.isScrollContainer = false
    calendarPView.selectionMode = MaterialCalendarView.SELECTION_MODE_MULTIPLE
    calendarPView.setOnDateChangedListener { widget, date, selected ->
        val calendar = Calendar.getInstance()
        calendar.time = date.date
    }


    for (i in weekArray.indices) {
        weekAry.add(WeekDataModel((i + 1), weekArray[i]))
    }
    weekAry.add(WeekDataModel(weekAry.size + 1, "Sun"))


    if (carePoint.week_days != null) {
        for (i in weekAry) {
            for (j in carePoint.week_days!!) {
                i.isSelected = false
                if (i.id == j) {
                    selectedWeekDays.add(i)
                    i.isSelected = true
                    break
                }
            }
            addedWeekDays.add(i)
        }

    }
    if (addedWeekDays.size > 0) weekAry = addedWeekDays

    weekAdapter = WeekAdapter(weekAry, object : WeekAdapter.WeekDaySelected {
        override fun onDaySelected(detail: ArrayList<WeekDataModel>) {
            selectedWeekDays.clear()
            for (i in detail) {
                if (i.isSelected) {
                    selectedWeekDays.add(i)
                }
            }
            selectedWeekDays.sortBy { it.id }
        }
    })
    weekdaysRV.adapter = weekAdapter

    monthAdapter(carePoint, dialog, monthRV)

    if (carePoint.repeat_end_date != null) {
        val dateSelected =
            SimpleDateFormat("yyyy-MM-dd").parse(carePoint.repeat_end_date!!)
        val endDate =
            dateSelected?.let { SimpleDateFormat("MM/dd/yyyy").format(it) }
        tvEndDate.text = endDate


    }
    tvEndDate.setOnClickListener {
        datePicker(tvEndDate, carePoint, dialog, monthRV)
    }
    leftV.setOnClickListener {

    }
    rightV.setOnClickListener {

    }

    radioGroupCheckId = if (!carePoint.month_dates.isNullOrEmpty())
        R.id.monthRB
    else if (!carePoint.week_days.isNullOrEmpty())
        R.id.weekRB
    else
        R.id.dayRB

    radioGroup.setOnCheckedChangeListener { viewButton, _ ->
        when (radioGroup.checkedRadioButtonId) {
            R.id.dayRB -> {
                if (radioGroupCheckId != R.id.dayRB) {
                    selectedMonthDates.clear()
                    monthAdapter.clearSelectedList()

                    selectedWeekDays.clear()
                    weekAdapter.clearSelectedList()
                }

                if (tvEndDate.text.toString().isEmpty()) {
                    showToast(getString(R.string.please_select_end_date_proceed))
                    radioGroup.clearCheck()
                } else {
                    value.type = RecurringEvent.Daily.value
                    weekdaysRV.isVisible = false
                    calenderCL.isVisible = false
                    monthRV.isVisible = false
                }

                radioGroupCheckId = R.id.dayRB
            }

            R.id.weekRB -> {
                if (radioGroupCheckId != R.id.weekRB) {
                    selectedMonthDates.clear()
                    monthAdapter.clearSelectedList()

                    selectedWeekDays.clear()
                    weekAdapter.clearSelectedList()
                }

                if (tvEndDate.text.toString().isEmpty()) {
                    showToast(getString(R.string.please_select_end_date_proceed))
                    radioGroup.clearCheck()
                } else {

                    val startDateDate = SimpleDateFormat("yyyy-MM-dd").parse(carePoint.date!!)
                    val lastDate = SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())

                    weekAdapter.setDayNameList(
                        CommonFunctions.getWeekDayNameList(
                            startDateDate,
                            lastDate
                        )
                    )


                    value.type = RecurringEvent.Weekly.value
                    Handler(Looper.getMainLooper()).postDelayed({
                        monthRV.isVisible = false
                        calenderCL.isVisible = false
                        weekdaysRV.isVisible = true
                    }, 100)
                }
                radioGroupCheckId = R.id.weekRB

            }

            R.id.monthRB -> {
                if (radioGroupCheckId != R.id.monthRB) {
                    selectedMonthDates.clear()
                    monthAdapter.clearSelectedList()

                    selectedWeekDays.clear()
                    weekAdapter.clearSelectedList()
                }

                if (tvEndDate.text.toString().isEmpty()) {
                    showToast(getString(R.string.please_select_end_date_proceed))
                    radioGroup.clearCheck()
                } else {
                    if (carePoint.date != null) {
                        val startDateDate = SimpleDateFormat("yyyy-MM-dd").parse(carePoint.date!!)
                        val lastDate =
                            SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())

                        monthAdapter.startEndDate(startDateDate!!, lastDate!!)
                    }

                    value.type = RecurringEvent.Monthly.value
                    Handler(Looper.getMainLooper()).postDelayed({
                        monthRV.isVisible = true
                        calenderCL.isVisible = false
                        weekdaysRV.isVisible = false
                    }, 100)

                }
                radioGroupCheckId = R.id.monthRB

            }

        }

    }
    if (carePoint.repeat_flag != null) {
        dayRB.isChecked = false
        weekRB.isChecked = false
        monthRB.isChecked = false

        when (carePoint.repeat_flag) {
            RecurringFlag.Daily.value -> {
                value.type = RecurringEvent.Daily.value
                dayRB.isChecked = true
            }

            RecurringFlag.Weekly.value -> {
                value.type = RecurringEvent.Weekly.value
                weekRB.isChecked = true
                Handler(Looper.getMainLooper()).postDelayed({

                    monthRV.isVisible = false
                    calenderCL.isVisible = false
                    weekdaysRV.isVisible = true
                }, 100)
            }

            RecurringFlag.Monthly.value -> {
                value.type = RecurringEvent.Monthly.value
                monthRB.isChecked = true
                Handler(Looper.getMainLooper()).postDelayed({
                    monthRV.isVisible = true
                    calenderCL.isVisible = false
                    weekdaysRV.isVisible = false
                }, 100)
            }

            else -> {

            }
        }

    }


    if (carePoint.month_dates != null) {
        val date = SimpleDateFormat("MM-yyyy").format(Calendar.getInstance().time)
        for (i in carePoint.month_dates!!) {
            val monthDate = i.toString().plus("-$date")
            val dateShow: Date = SimpleDateFormat("dd-MM-yyyy").parse(monthDate) as Date
            val calendar = Calendar.getInstance()
            calendar.time = dateShow
            calendarPView.setDateSelected(calendar, true)
            val monthModel = MonthModel()
            monthModel.monthDate = i.toString()
            monthModel.isSelected = true
            selectedMonthDates.add(monthModel)
        }
        calenderCL.isVisible = false
        weekdaysRV.isVisible = false
    }


    btnYes.setOnClickListener {
        when (value.type) {
            RecurringEvent.None.value -> {
                dialog.dismiss()
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
                if (selectedWeekDays.size <= 0) {
                    showError(requireContext(), getString(R.string.please_atleast_one_weekday))
                } else {

                    val date: ArrayList<Int> = arrayListOf()
                    val days: ArrayList<String> = arrayListOf()
                    for (i in selectedWeekDays) {
                        date.add(i.id!!)
                        days.add(i.name!!)
                    }

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

            RecurringEvent.Monthly.value -> {

                if (selectedMonthDates == null || selectedMonthDates.size <= 0) {
                    showError(requireContext(), getString(R.string.please_select_atleast_one_date))
                } else {

                    val date: ArrayList<Int> = arrayListOf()
                    for (i in selectedMonthDates) {
                        val currentDateCalendar = Calendar.getInstance()
                        currentDateCalendar.set(Calendar.DAY_OF_MONTH, i.monthDate.toInt());
                        date.add(SimpleDateFormat("dd").format(currentDateCalendar.time).toInt())
                    }
                    date.sort()
                    val lastDate = SimpleDateFormat("MM/dd/yyyy").parse(tvEndDate.text.toString())
                    val startDateDate = SimpleDateFormat("yyyy-MM-dd").parse(carePoint.date!!)
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
        dialog.dismiss()
    }

    dialog.show()
}

private fun EditCarePointFragment.monthAdapter(
    carePoint: AddedEventModel,
    dialog: Dialog,
    monthRV: RecyclerView
) {
    val monthArrayList: ArrayList<MonthModel> = ArrayList()
    for (i in 1..31) {
        val monthModel = MonthModel()
        monthModel.monthDate = i.toString()
        if (!carePoint.month_dates.isNullOrEmpty()) {
            monthModel.isSelected = carePoint.month_dates?.contains(i)!!
            selectedMonthDates.add(monthModel)
        }

        monthArrayList.add(monthModel)
    }


    monthAdapter =
        MonthAdapter(dialog.context, monthArrayList, object : MonthAdapter.selectedMonth {
            override fun onMonthSelected(monthModel: MonthModel, position: Int) {
                val listOfStringMonthDate: ArrayList<String> = ArrayList()
                selectedMonthDates.forEach {
                    listOfStringMonthDate.add(it.monthDate)
                }
                var selectedPosition = 0

                listOfStringMonthDate.forEachIndexed { index, s ->
                    if (s == monthModel.monthDate) {
                        selectedPosition = index
                        return@forEachIndexed
                    }
                }

                if (listOfStringMonthDate.contains(monthModel.monthDate)) {
                    selectedMonthDates.removeAt(selectedPosition)
                } else
                    selectedMonthDates.add(monthModel)
            }
        })


    monthRV.adapter = monthAdapter
}

@SuppressLint("SetTextI18n", "SimpleDateFormat", "NotifyDataSetChanged")
fun EditCarePointFragment.datePicker(
    tvEndDate: AppCompatTextView, carePoint: AddedEventModel, dialog: Dialog,
    monthRV: RecyclerView
) {
    val c = Calendar.getInstance()
    val mYear = c[Calendar.YEAR]
    val mMonth = c[Calendar.MONTH]
    val mDay = c[Calendar.DAY_OF_MONTH]

    if (carePoint.date != null) {
        val endDate =
            SimpleDateFormat("yyyy-MM-dd").parse(carePoint.date!!)
        c.time = endDate!!
        c.add(Calendar.DATE, 1)
    }

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
//                monthAdapter(carePoint, dialog, monthRV)

                selectedWeekDays.clear()
                weekAdapter.clearSelectedList()
                weekAdapter.notifyDataSetChanged()
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
    datePickerDialog.datePicker.minDate = c.timeInMillis
    datePickerDialog.show()
}
