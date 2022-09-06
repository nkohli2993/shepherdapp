package com.shepherd.app.ui.component.schedule_medicine

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.data.dto.med_list.ScheduledMedicationRequestModel
import com.shepherd.app.data.dto.med_list.UpdateScheduledMedList
import com.shepherd.app.data.dto.med_list.get_medication_detail.Payload
import com.shepherd.app.data.dto.med_list.schedule_medlist.*
import com.shepherd.app.databinding.FragmentSchedulweMedicineBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.schedule_medicine.adapter.*
import com.shepherd.app.utils.FrequencyType
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nikita kohli on 08-08-22
 */

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged,SetTextI18n,SimpleDateFormat")
class ScheduleMedicineFragment : BaseFragment<FragmentSchedulweMedicineBinding>(),
    View.OnClickListener, FrequencyAdapter.selectedFrequency, DaysAdapter.SelectedDay {

    private lateinit var fragmentScheduleMedicineBinding: FragmentSchedulweMedicineBinding
    private var dayAdapter: DaysAdapter? = null
    private var dayList: ArrayList<DayList> = arrayListOf()
    private var selectedDose: DoseList? = null
    private var doseAdapter: DoseAdapter? = null
    private var doseList: ArrayList<DoseList> = arrayListOf()
    private var currentPage: Int = 0
    private var totalPage: Int = 0
    private var total: Int = 0
    private val frequencyList: ArrayList<FrequencyData> = arrayListOf()
    private var timeAdapter: TimeAdapter? = null
    private var pageNumber = 1
    private var limit = 10
    private var frequencyId: Int? = null
    private var doseID: String? = null
    private var selectedDoseId: String? = null
    private var selectedDoseTypeId: String? = null
    private var daysIds: String? = null
    private val medicationViewModel: AddMedicationViewModel by viewModels()
    private val args: ScheduleMedicineFragmentArgs by navArgs()
    private var selectedMedList: Medlist? = null
    private var timeList: MutableList<TimeSelectedlist> = arrayListOf()
    private var addedTimeList: MutableList<TimeSelectedlist> = arrayListOf()
    private var selectedDateFormat = SimpleDateFormat("MM-dd-yyyy")
    private var selectedDateTimeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
    private var serverDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var medicationId: Int? = null
    private var isTimeChanged: Boolean? = null
    private var isDoseChanged: Boolean? = null
    private var dosageAdapter: DosageQtyTypeAdapter? = null
    private var payLoad: Payload? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentScheduleMedicineBinding =
            FragmentSchedulweMedicineBinding.inflate(inflater, container, false)

        return fragmentScheduleMedicineBinding.root
    }

    override fun observeViewModel() {

        observe(medicationViewModel.timeSelectedlist, ::selectedTime)
        observe(medicationViewModel.doseListData, ::selectedDoseData)
        observe(medicationViewModel.dayListSelectedData, ::selectedDay)
        medicationViewModel.getDoseListResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    it.data.payload.let { payload ->
                        doseList = payload?.dosages!!
                        total = payload.total
                        currentPage = payload.currentPage
                        totalPage = payload.totalPages
                    }

                    if (doseList.isEmpty()) return@observeEvent
                    doseAdapter?.addData(doseList)
                    doseList.add(0, DoseList(id = -1, name = "Dose Qty"))
                    dosageAdapter =
                        DosageQtyTypeAdapter(
                            requireContext(),
                            R.layout.vehicle_spinner_drop_view_item,
                            doseList
                        )

                    fragmentScheduleMedicineBinding.qtySpinner.adapter = dosageAdapter
                    fragmentScheduleMedicineBinding.typeSpinner.adapter = dosageAdapter
                    val doseQty = dosageAdapter?.getItem(0)
                    if (doseQty != null) selectedDoseId = doseQty.id.toString()

                    if (doseQty != null) selectedDoseTypeId = doseQty.id.toString()
                }
            }
        }
        medicationViewModel.addScheduledMedicationResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(
                        requireContext(),
                        getString(R.string.scheduled_medication_created_successfully)
                    )
                    findNavController().navigate(R.id.action_nav_schedule_medication_to_nav_my_medlist)
                }
            }
        }

        //observer to show added medication detail
        medicationViewModel.getMedicationDetailResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(requireContext(), it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    payLoad = it.data.payload
                    if (payLoad != null) {
                        selectedDoseId = payLoad!!.dosage_id.toString()
                        doseID = payLoad!!.dosage_id.toString()
                        // fragmentScheduleMedicineBinding.doseTV.text = payLoad!!.dosage.name
                        setFrequency(payLoad!!.frequency.toString())
                        if (payLoad!!.end_date != null) {
                            setEndDate(payLoad!!.end_date!!)
                        }
                        timeList.clear()
                        addedTimeList.clear()
                        for (i in payLoad?.time!!) {
                            timeList.add(
                                TimeSelectedlist(
                                    timeList.size,
                                    i.time,
                                    i.hour.lowercase()
                                )
                            )
                            // added data to addedTimeList to maintain added time for medication
                            addedTimeList.add(
                                TimeSelectedlist(
                                    timeList.size,
                                    i.time,
                                    i.hour.lowercase()
                                )
                            )
                        }
                        setTimeAdapter()
                        addDays(isEdit = true, payLoad!!.days)
                        setDayAdapter()
                        fragmentScheduleMedicineBinding.etNote.setText(payLoad!!.note)
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun initViewBinding() {
        fragmentScheduleMedicineBinding.listener = this
        if (args.medlist != null) {
            selectedMedList = args.medlist
            fragmentScheduleMedicineBinding.tvMedTitle.text = selectedMedList?.name
        }
        frequencyId = FrequencyType.ONCE.value.toInt()
        fragmentScheduleMedicineBinding.btnSubmit.text = getString(R.string.add_medication)
        fragmentScheduleMedicineBinding.tvMedList.text = getString(R.string.schedule_medication)
        if (args.medicationId != null) {
            medicationId = args.medicationId!!.toInt()
            fragmentScheduleMedicineBinding.tvMedList.text = getString(R.string.update_medication)
            fragmentScheduleMedicineBinding.btnSubmit.text = getString(R.string.update_medication)
        }
        addFrequencyType()
        fragmentScheduleMedicineBinding.frequencyRV.adapter = FrequencyAdapter(
            requireContext(),
            this,
            frequencyList
        )

        medicationViewModel.getAllDoseList(pageNumber, limit)

        fragmentScheduleMedicineBinding.etNote.setOnTouchListener { view, event ->
            //check days view open
            if (fragmentScheduleMedicineBinding.daysRV.visibility == View.VISIBLE) {
                fragmentScheduleMedicineBinding.daysRV.visibility = View.GONE
                rotate(0f, fragmentScheduleMedicineBinding.dayIM)
            }

            view.parent.requestDisallowInterceptTouchEvent(true)    /// for edit text scroll issue
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }

            return@setOnTouchListener false
        }
        //set data according to value added
        if (medicationId != null) {
            medicationId?.let { medicationViewModel.getMedicationDetail(it) }
        } else {
            timeList.add(TimeSelectedlist())
            addDays()
            setDayAdapter()
            setTimeAdapter()
        }
        setDoseAdapter()

        fragmentScheduleMedicineBinding.qtySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //add days to day list
                    selectedDoseId = doseList[p2].id.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
        fragmentScheduleMedicineBinding.typeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //add days to day list
                    selectedDoseTypeId = doseList[p2].id.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

    }


    private fun setEndDate(date: String) {
        val formattedDate = serverDateFormat.parse(date)!!
        fragmentScheduleMedicineBinding.endDate.text =
            selectedDateFormat.format(formattedDate)
    }

    private fun setFrequency(frequencyValue: String) {
        val frequency = when (frequencyValue) {
            FrequencyType.ONCE.value -> {
                frequencyId = FrequencyType.ONCE.value.toInt()
                getString(R.string.once_a_day)
            }
            FrequencyType.TWICE.value -> {
                frequencyId = FrequencyType.TWICE.value.toInt()
                getString(R.string.twice_a_day)
            }
            FrequencyType.THRICE.value -> {
                frequencyId = FrequencyType.THRICE.value.toInt()
                getString(R.string.three_times_a_day)
            }
            FrequencyType.FOUR.value -> {
                frequencyId = FrequencyType.FOUR.value.toInt()
                getString(R.string.four_times_a_day)
            }
            else -> {
                frequencyId = FrequencyType.ONCE.value.toInt()
                getString(R.string.once_a_day)
            }
        }
        fragmentScheduleMedicineBinding.frequencyET.text = frequency
    }

    private fun setDoseAdapter() {
        doseAdapter = DoseAdapter(medicationViewModel, requireContext(), doseList)
        fragmentScheduleMedicineBinding.doseRV.adapter = doseAdapter
    }

    private fun setTimeAdapter() {
        timeAdapter = TimeAdapter(medicationViewModel, requireContext(), timeList)
        fragmentScheduleMedicineBinding.recycleviewTime.adapter = timeAdapter
    }

    private fun setDayAdapter() {
        dayList.sortWith { o1, o2 ->
            if (o1.id == null) 0 else o1.id!!
                .compareTo(o2.id!!)
        }
        dayAdapter = DaysAdapter(medicationViewModel, requireContext(), this, dayList)
        fragmentScheduleMedicineBinding.daysRV.adapter = dayAdapter
    }

    private fun addFrequencyType() {
        frequencyList.add(
            FrequencyData(
                frequencyList.size,
                getString(R.string.once_a_day),
                FrequencyType.ONCE.value.toInt()
            )
        )
        frequencyList.add(
            FrequencyData(
                frequencyList.size,
                getString(R.string.twice_a_day),
                FrequencyType.TWICE.value.toInt()
            )
        )
        frequencyList.add(
            FrequencyData(
                frequencyList.size,
                getString(R.string.three_times_a_day),
                FrequencyType.THRICE.value.toInt()
            )
        )
        frequencyList.add(
            FrequencyData(
                frequencyList.size,
                getString(R.string.four_times_a_day),
                FrequencyType.FOUR.value.toInt()
            )
        )
        frequencyList.add(
            FrequencyData(
                frequencyList.size,
                getString(R.string.as_needed),
                FrequencyType.FIVE.value.toInt()
            )
        )
    }

    // to get gap between two dates
    private fun printDifference(startDate: Date, endDate: Date): Long {
        val different = endDate.time - startDate.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        return different / daysInMilli
    }

    private fun addDays(isEdit: Boolean = false, dayId: String = "") {
        //to get days according to date selected
        fragmentScheduleMedicineBinding.daysTV.text = ""
        if (fragmentScheduleMedicineBinding.endDate.text.isEmpty()) {
            dayList = addWeekDays()
        } else {
            val addedEndDate: Date = selectedDateFormat.parse(
                fragmentScheduleMedicineBinding.endDate.text.toString().trim()
            )!!
            val currentDate =
                selectedDateFormat.parse(selectedDateFormat.format(Calendar.getInstance().time))
            if (printDifference(
                    currentDate!!,
                    addedEndDate
                ) >= 7L
            ) {
                dayList = addWeekDays()
            } else {
                val cal = Calendar.getInstance()
                val availDayList = getDates(
                    serverDateFormat.format(cal.time),
                    serverDateFormat.format(addedEndDate)
                )
                val allDayList = addWeekDays()
                dayList.clear()
                val notAvailableDays: ArrayList<DayList> = ArrayList()
                for (allDay in allDayList) {
                    var found = false
                    for (availableDay in availDayList) {
                        if (allDay.id === availableDay.id) {
                            found = true
                        }
                    }
                    if (!found) {
                        allDay.isClickabled = false
                        notAvailableDays.add(allDay)
                    }
                }
                dayList.addAll(availDayList)
                dayList.addAll(notAvailableDays)
            }
        }
        val selectedDays: ArrayList<String> = arrayListOf()
        if (isEdit) {
            daysIds = dayId
            for (i in stringToWords(dayId)) {
                for (j in 0 until dayList.size) {
                    if (i.toInt() == dayList[j].id) {
                        dayList[j].isSelected = true
                        selectedDays.add(dayList[j].time!!)
                        break
                    }
                }
            }
            if (selectedDays.size > 0) {
                fragmentScheduleMedicineBinding.daysTV.text =
                    selectedDays.joinToString().replace(" ", "")
            } else {
                fragmentScheduleMedicineBinding.daysTV.text =
                    ""
            }
        }
    }

    private fun addWeekDays(): ArrayList<DayList> {
        val dayList: ArrayList<DayList> = arrayListOf()
        dayList.add(DayList(dayList.size + 1, "Monday", isSelected = false, isClickabled = true))
        dayList.add(DayList(dayList.size + 1, "Tuesday", isSelected = false, isClickabled = true))
        dayList.add(
            DayList(
                dayList.size + 1,
                "Wednesday",
                isSelected = false,
                isClickabled = true
            )
        )
        dayList.add(DayList(dayList.size + 1, "Thursday", isSelected = false, isClickabled = true))
        dayList.add(DayList(dayList.size + 1, "Friday", isSelected = false, isClickabled = true))
        dayList.add(DayList(dayList.size + 1, "Saturday", isSelected = false, isClickabled = true))
        dayList.add(DayList(dayList.size + 1, "Sunday", isSelected = false, isClickabled = true))
        return dayList
    }

    private fun stringToWords(s: String) = s.trim().splitToSequence(',')
        .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
        .toList()

    private fun selectedTime(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            timePicker(it)
        }
    }

    private fun selectedDay(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            if (dayList[it].isClickabled) {
                dayList[it].isSelected = !dayList[it].isSelected

                val selected: ArrayList<String> = arrayListOf()
                val selectedDays: ArrayList<String> = arrayListOf()
                for (i in dayList) {
                    if (i.isSelected) {
                        selected.add(i.id.toString())
                        selectedDays.add(i.time.toString())
                    }
                }
                if (selectedDays.size > 0) {
                    daysIds = selected.joinToString().replace(" ", "")
                    fragmentScheduleMedicineBinding.daysTV.text =
                        selectedDays.joinToString().replace(" ", "")
                } else {
                    daysIds = null
                    fragmentScheduleMedicineBinding.daysTV.text = ""
                }
            } else {
                showError(
                    requireContext(),
                    getString(R.string.this_day_does_not_lie_between_selected_end_date_of_medication)
                )
            }
            dayAdapter?.notifyDataSetChanged()

        }
    }

    private fun selectedDoseData(navigateEvent: SingleEvent<DoseList>) {
        navigateEvent.getContentIfNotHandled()?.let {
            //show selected dose
            doseID = it.id.toString()
            selectedDose = it
//            fragmentScheduleMedicineBinding.doseTV.text = selectedDose?.name ?: ""
            showDoseView()
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_schedulwe_medicine
    }

    private fun rotate(degree: Float, image: AppCompatImageView) {
        val rotateAnim = RotateAnimation(
            0.0f, degree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 0
        rotateAnim.fillAfter = true
        image.startAnimation(rotateAnim)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.endDate -> {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]
                c.add(Calendar.DATE, 1)
                val datePickerDialog = DatePickerDialog(
                    requireActivity(), R.style.datepicker,
                    { _, year, monthOfYear, dayOfMonth ->
                        fragmentScheduleMedicineBinding.endDate.text =
                            "${
                                if (monthOfYear + 1 < 10) {
                                    "0${(monthOfYear + 1)}"
                                } else {
                                    (monthOfYear + 1)
                                }
                            }-${
                                if (dayOfMonth + 1 < 10) {
                                    "0$dayOfMonth"
                                } else {
                                    dayOfMonth
                                }
                            }-$year"
                        addDays()
                        setDayAdapter()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.datePicker.minDate = c.timeInMillis
                datePickerDialog.show()
            }
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSubmit -> {
                if (isValid) {
                    // call create medication api for scheduling
                    val timeAddedList: ArrayList<Time> = arrayListOf()
                    for (i in timeList) {
                        timeAddedList.add(Time(i.time, i.isAmPM))
                    }
                    var endDate: String? = null
                    if (fragmentScheduleMedicineBinding.endDate.text.toString().trim()
                            .isNotEmpty()
                    ) {
                        val formattedDate: Date = selectedDateFormat.parse(
                            fragmentScheduleMedicineBinding.endDate.text.toString().trim()
                        )!!
                        // check current day medication creation
                        if (formattedDate == selectedDateFormat.parse(
                                selectedDateFormat.format(
                                    Calendar.getInstance().time
                                )
                            )
                        ) {
                            // check for current day entry for medication
                            if (timeAddedList.size == 1) {
                                val currentDateTime = selectedDateTimeFormat.parse(
                                    selectedDateTimeFormat.format(Calendar.getInstance().time)
                                )
                                val medicationDateTime = selectedDateTimeFormat.parse(
                                    serverDateFormat.format(formattedDate) + " " + timeAddedList[0].time + " " + timeAddedList[0].hour
                                )

                                if (medicationDateTime!!.before(currentDateTime)) {
                                    showError(
                                        requireContext(),
                                        getString(R.string.please_add_medication_for_future_time)
                                    )
                                    return
                                }
                            }
                        }
                        endDate = serverDateFormat.format(formattedDate)
                    }

                    if (medicationId != null) {
                        val timeChangeList: ArrayList<TimeSelectedlist> = ArrayList()
                        for (addedTime in addedTimeList) {
                            var found = false
                            for (newTime in timeList) {
                                if (addedTime.time.plus(" ${addedTime.isAmPM}") == newTime.time.plus(
                                        " ${newTime.isAmPM}"
                                    )
                                ) {
                                    found = true
                                }
                            }
                            if (!found) {
                                timeChangeList.add(addedTime)
                            }
                        }

                        if (timeChangeList.size > 0) {
                            isTimeChanged = true
                        }
                        when {
                            (doseID ?: "0").toInt() != (selectedDoseId ?: "0").toInt() -> {
                                isDoseChanged = true
                            }
                        }
                        val scheduledMedication = UpdateScheduledMedList(
                            doseID!!,
                            frequencyId!!.toString(),
                            daysIds!!,
                            timeAddedList,
                            fragmentScheduleMedicineBinding.etNote.text.toString().trim(),
                            endDate,
                            isTimeChanged,
                            isDoseChanged
                        )
                        medicationViewModel.updateScheduledMedication(
                            scheduledMedication,
                            medicationId!!
                        )
                    } else {
                        val scheduledMedication =
                            ScheduledMedicationRequestModel(
                                medicationViewModel.getLovedOneUUId(),
                                selectedDoseId!!,
                                frequencyId!!.toString(),
                                selectedMedList?.id.toString(),
                                daysIds!!,
                                timeAddedList,
                                fragmentScheduleMedicineBinding.etNote.text.toString().trim(),
                                endDate
                            )
                        medicationViewModel.addScheduledMedication(scheduledMedication)
                    }

                }
            }
/*
            R.id.doseTV -> {
                showDoseView()
            }
*/
            R.id.frequencyET -> {
                showFrequencyView()
            }
            R.id.daysTV -> {
                showDayView()
            }
        }
    }


    private fun timePicker(position: Int) {
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context, R.style.datepicker,
            { _, hourOfDay, selectedMinute ->
                val amPm = if (hourOfDay < 12) {
                    "am"
                } else {
                    "pm"
                }

                for (i in timeList) {
                    if (i.time == String.format(
                            "%02d:%02d",
                            if (hourOfDay < 12) hourOfDay else (hourOfDay - 12),
                            selectedMinute
                        ) && i.isAmPM == amPm
                    ) {
                        showError(
                            requireContext(),
                            getString(R.string.timing_already_added_for_dose_)
                        )
                        timeList[position].time = ""
                        timeList[position].isAmPM = ""
                        timeAdapter!!.notifyDataSetChanged()
                        return@TimePickerDialog
                    }
                }
                if (hourOfDay < 12) {
                    timeList[position].time = String.format("%02d:%02d", hourOfDay, selectedMinute)
                    timeList[position].isAmPM = "am"
                } else {
                    timeList[position].time =
                        String.format("%02d:%02d", hourOfDay - 12, selectedMinute)
                    timeList[position].isAmPM = "pm"
                }
                timeList[position].isAmPM = amPm
                timeAdapter!!.notifyDataSetChanged()
            }, hour,
            minute, false
        )
        mTimePicker.show()
    }

    private val isValid: Boolean
        get() {
            when {
/*
                doseID == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_dose_for_medication)
                    )
                }
*/
                selectedDoseId == null || selectedDoseId == "-1" -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_dose_for_medication)
                    )
                }
                selectedDoseTypeId == null || selectedDoseTypeId == "-1" -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_dose_type_for_medication)
                    )
                }
                frequencyId == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_frequcny_for_dose)
                    )
                }
/*
                timeList.size == 1 && timeListCheck() -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_timing_medication)
                    )
                }

                timeList.size > 1 && timeListCheck() -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_timing_all_medication)
                    )
                }
*/
                daysIds == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_days_for_medication)
                    )
                }
/*
                fragmentScheduleMedicineBinding.etNote.text.toString().trim().isEmpty() -> {
                    fragmentScheduleMedicineBinding.etNote.error =
                        getString(R.string.please_enter_med_list_notes)
                    fragmentScheduleMedicineBinding.etNote.requestFocus()
                }
*/

                else -> {
                    return true
                }
            }
            return false
        }

    private fun timeListCheck(): Boolean {
        var allFiled = false

        for (i in timeList) {
            if (i.time.isNullOrEmpty()) {
                allFiled = true
                break
            }
        }
        return allFiled
    }

/*    //check time added or removed from list
    override fun onSelected(position: Int) {
        frequencyId = frequencyList[position].time!!
        val list: ArrayList<TimeSelectedlist> = arrayListOf()
        val timeCount = frequencyList[position].time!!
        for (i in 0 until timeCount) {
            list.add(TimeSelectedlist())
        }
        timeList.clear()
        timeList.addAll(list)
        timeAdapter = null
        setTimeAdapter()
        showFrequencyView()
        fragmentScheduleMedicineBinding.frequencyET.text = frequencyList[position].name
    }*/

    //check time added or removed from list
    override fun onSelected(position: Int) {
        frequencyId = frequencyList[position].time!!

        if (frequencyList[position].time == FrequencyType.FIVE.value.toInt()) {
            fragmentScheduleMedicineBinding.recycleviewTime.visibility = View.GONE
            fragmentScheduleMedicineBinding.tvTime.visibility = View.GONE
        } else {
            fragmentScheduleMedicineBinding.recycleviewTime.visibility = View.VISIBLE
            fragmentScheduleMedicineBinding.tvTime.visibility = View.VISIBLE
            if (timeList.size > frequencyList[position].time!!.toInt()) {
                timeAdapter?.removeData(timeList.size - frequencyList[position].time!!.toInt())
                showFrequencyView()
                fragmentScheduleMedicineBinding.frequencyET.text = frequencyList[position].name
            } else {
                val list: ArrayList<TimeSelectedlist> = arrayListOf()
                val timeCount = frequencyList[position].time!! - timeList.size
                for (i in 0 until timeCount) {
                    list.add(TimeSelectedlist())
                }
                timeAdapter?.addData(list)
            }
        }
        showFrequencyView()
        fragmentScheduleMedicineBinding.frequencyET.text = frequencyList[position].name
    }

    private fun showFrequencyView() {
        if (fragmentScheduleMedicineBinding.frequencyRV.visibility == View.VISIBLE) {
            fragmentScheduleMedicineBinding.frequencyRV.visibility = View.GONE
            rotate(0f, fragmentScheduleMedicineBinding.frequencyIM)
        } else {
            fragmentScheduleMedicineBinding.frequencyRV.visibility = View.VISIBLE
            rotate(180f, fragmentScheduleMedicineBinding.frequencyIM)
        }
        hideKeyboard(fragmentScheduleMedicineBinding.scrollView)
    }

    private fun showDayView() {
        if (fragmentScheduleMedicineBinding.daysRV.visibility == View.VISIBLE) {
            fragmentScheduleMedicineBinding.daysRV.visibility = View.GONE
            rotate(0f, fragmentScheduleMedicineBinding.dayIM)
        } else {
            fragmentScheduleMedicineBinding.daysRV.visibility = View.VISIBLE
            rotate(180f, fragmentScheduleMedicineBinding.dayIM)
        }
        hideKeyboard(fragmentScheduleMedicineBinding.scrollView)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showDoseView() {
        if (fragmentScheduleMedicineBinding.doseRV.visibility == View.VISIBLE) {
            fragmentScheduleMedicineBinding.doseRV.visibility = View.GONE
            rotate(0f, fragmentScheduleMedicineBinding.doseIM)
        } else {
            fragmentScheduleMedicineBinding.doseRV.visibility = View.VISIBLE
            rotate(180f, fragmentScheduleMedicineBinding.doseIM)
        }

        hideKeyboard(fragmentScheduleMedicineBinding.scrollView)
    }

    private fun getDates(dateString1: String, dateString2: String): ArrayList<DayList> {
        val dates = ArrayList<DayList>()
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = serverDateFormat.parse(dateString1)
            date2 = serverDateFormat.parse(dateString2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1!!
        val cal2 = Calendar.getInstance()
        cal2.time = date2!!
        while (!cal1.after(cal2)) {
            val id = when (SimpleDateFormat("EEE").format(cal1.time)) {
                "Mon" -> 1
                "Tue" -> 2
                "Wed" -> 3
                "Thu" -> 4
                "Fri" -> 5
                "Sat" -> 6
                else -> 7
            }
            dates.add(
                DayList(
                    id, SimpleDateFormat("EEEE").format(cal1.time),
                    isSelected = false,
                    isClickabled = true
                )
            )
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

    override fun selected(position: Int) {
        if (dayList[position].isClickabled) {
            dayList[position].isSelected = !dayList[position].isSelected
            dayAdapter!!.notifyDataSetChanged()
            val selected: ArrayList<String> = arrayListOf()
            val selectedDays: ArrayList<String> = arrayListOf()
            for (i in dayList) {
                if (i.isSelected) {
                    selected.add(i.id.toString())
                    selectedDays.add(i.time.toString())
                }
            }
            if (selectedDays.size > 0) {
                daysIds = selected.joinToString().replace(" ", "")
                fragmentScheduleMedicineBinding.daysTV.text =
                    selectedDays.joinToString().replace(" ", "")
            } else {
                fragmentScheduleMedicineBinding.daysTV.text = ""
            }
        } else {
            showError(
                requireContext(),
                "This day does not lie between selected end date of medication."
            )
        }

    }

}