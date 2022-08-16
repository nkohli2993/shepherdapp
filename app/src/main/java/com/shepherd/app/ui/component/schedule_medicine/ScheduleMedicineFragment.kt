package com.shepherd.app.ui.component.schedule_medicine

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.data.dto.med_list.ScheduledMedicationRequestModel
import com.shepherd.app.data.dto.med_list.UpdateScheduledMedList
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.data.dto.med_list.schedule_medlist.*
import com.shepherd.app.databinding.FragmentSchedulweMedicineBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.schedule_medicine.adapter.DaysAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.DoseAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.FrequencyAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.TimeAdapter
import com.shepherd.app.utils.FrequencyType
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
/**
 * Created by Nikita kohli on 08-08-22
 */

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged,SetTextI18n,SimpleDateFormat")
class ScheduleMedicineFragment : BaseFragment<FragmentSchedulweMedicineBinding>(),
    View.OnClickListener, FrequencyAdapter.selectedFrequency {
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
    private var daysIds: String? = null
    private val medicationViewModel: AddMedicationViewModel by viewModels()
    private val args: ScheduleMedicineFragmentArgs by navArgs()
    private var selectedMedList: Medlist? = null
    private var addedMedication: MedListReminder? = null
    private var medicationPayload: Payload? = null
    private var timeList: MutableList<TimeSelectedlist> = arrayListOf()
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
                    // Redirect to MedList reminder Screen
                    findNavController().navigate(R.id.action_nav_schedule_medication_to_nav_my_medlist)
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

        if (args.medicationScheduled != null) {
            //set paymload data
            addedMedication = args.medicationScheduled
            fragmentScheduleMedicineBinding.tvMedTitle.text = addedMedication?.medlist?.name
        }
        if (args.medicationScheduledPayload != null) {
            //set paymload data
            medicationPayload = args.medicationScheduledPayload
            fragmentScheduleMedicineBinding.tvMedTitle.text = medicationPayload?.medlist?.name
        }

        addFrequencyType()
        fragmentScheduleMedicineBinding.frequencyRV.adapter = FrequencyAdapter(
            requireContext(),
            this,
            frequencyList
        )

        medicationViewModel.getAllDoseList(pageNumber, limit)

        fragmentScheduleMedicineBinding.etNote.setOnTouchListener { _, _ ->
            //check days view open
            if (fragmentScheduleMedicineBinding.daysRV.visibility == View.VISIBLE) {
                fragmentScheduleMedicineBinding.daysRV.visibility = View.GONE
                rotate(0f, fragmentScheduleMedicineBinding.dayIM)
            }
            return@setOnTouchListener false
        }
        //set data according to value added
        if (addedMedication != null) {
            doseID = addedMedication!!.dosageId.toString()
            fragmentScheduleMedicineBinding.doseTV.text = addedMedication!!.dosage!!.name
            setFrequency(addedMedication?.frequency!!)
            if (addedMedication?.endDate != null) {
                setEndDate(addedMedication?.endDate!!)
            }

            timeList.clear()
            timeList.add(
                TimeSelectedlist(
                    timeList.size,
                    addedMedication?.time!!.time,
                    addedMedication?.time!!.hour!!.lowercase()
                )
            )
            setTimeAdapter()
            addDays(isEdit = true, addedMedication?.days!!)
            setDayAdapter()
            fragmentScheduleMedicineBinding.etNote.setText(addedMedication?.note)
        } else if (medicationPayload != null) {
            doseID = medicationPayload!!.dosageId.toString()
            fragmentScheduleMedicineBinding.doseTV.text = medicationPayload!!.dosage!!.name
            setFrequency(medicationPayload?.frequency!!)
            if (medicationPayload?.endDate != null) {
                setEndDate(medicationPayload?.endDate!!)
            }
            timeList.clear()
            for (i in medicationPayload?.time!!) {
                timeList.add(TimeSelectedlist(timeList.size, i.time, i.hour!!.lowercase()))
            }
            setTimeAdapter()
            addDays(isEdit = true, medicationPayload?.days!!)
            setDayAdapter()
            fragmentScheduleMedicineBinding.etNote.setText(medicationPayload?.note)
        } else {
            timeList.add(TimeSelectedlist())
            addDays()
            setDayAdapter()
            setTimeAdapter()
        }

//        Log.e("catch_exception", getDates("2022-08-10", "2022-08-15").toString())
        setDoseAdapter()
    }


    private fun setEndDate(date: String) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd").parse(date)!!
        fragmentScheduleMedicineBinding.endDate.text =
            SimpleDateFormat("dd-MM-yyyy").format(formattedDate)
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
        Collections.sort(dayList,
            Comparator<DayList?> { o1, o2 ->
                if (o1.id == null || o2.id!! == null) 0 else o1.id!!
                    .compareTo(o2.id!!)
            })
        dayAdapter = DaysAdapter(medicationViewModel, requireContext(), dayList)
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
    }

    // to get gap between two dates
    open fun printDifference(startDate: Date, endDate: Date): Long {
        var different = endDate.time - startDate.time
        val secondsInMilli: Long = 1000
        val minutesInMilli = secondsInMilli * 60
        val hoursInMilli = minutesInMilli * 60
        val daysInMilli = hoursInMilli * 24
        val elapsedDays = different / daysInMilli
        Log.e("catch_exception", "days: $elapsedDays")
        return elapsedDays
    }

    private fun addDays(isEdit: Boolean = false, dayId: String = "") {
        //to get days according to date selected
        if (fragmentScheduleMedicineBinding.endDate.text.isEmpty()) {
            addWeekDays()
        } else {
            var dateFormatDD = SimpleDateFormat("dd-MM-yyyy")
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val formattedDate: Date = dateFormatDD.parse(
                fragmentScheduleMedicineBinding.endDate.text.toString().trim()
            )!!
            val currentDate = dateFormatDD.format(Calendar.getInstance().time)
            val date = dateFormatDD.parse(currentDate)
            if (printDifference(date, formattedDate) >= 7L) {  // checked days to remove date duplicity
                addWeekDays()
            } else {
                dayList = getDates(
                    dateFormat.format(Calendar.getInstance().time),
                    dateFormat.format(formattedDate)
                )
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
            fragmentScheduleMedicineBinding.daysTV.text =
                selectedDays.joinToString().replace(" ", "")
        }
    }

    private fun addWeekDays() {
        dayList.clear()
        dayList.add(DayList(dayList.size + 1, "Monday", false))
        dayList.add(DayList(dayList.size + 1, "Tuesday", false))
        dayList.add(DayList(dayList.size + 1, "Wednesday", false))
        dayList.add(DayList(dayList.size + 1, "Thursday", false))
        dayList.add(DayList(dayList.size + 1, "Friday", false))
        dayList.add(DayList(dayList.size + 1, "Saturday", false))
        dayList.add(DayList(dayList.size + 1, "Sunday", false))
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
            dayList[it].isSelected = !dayList[it].isSelected
            dayAdapter!!.notifyDataSetChanged()
            val selected: ArrayList<String> = arrayListOf()
            val selectedDays: ArrayList<String> = arrayListOf()
            for (i in dayList) {
                if (i.isSelected) {
                    selected.add(i.id.toString())
                    selectedDays.add(i.time.toString())
                }
            }
            if (selected.size > 0) {
                daysIds = selected.joinToString().replace(" ", "")
                fragmentScheduleMedicineBinding.daysTV.text =
                    selectedDays.joinToString().replace(" ", "")
            }
        }
    }

    private fun selectedDoseData(navigateEvent: SingleEvent<DoseList>) {
        navigateEvent.getContentIfNotHandled()?.let {
            //show selected dose
            doseID = it.id.toString()
            selectedDose = it
            fragmentScheduleMedicineBinding.doseTV.text = selectedDose?.name ?: ""
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

                val datePickerDialog = DatePickerDialog(
                    requireActivity(), R.style.datepicker,
                    { _, year, monthOfYear, dayOfMonth ->
                        fragmentScheduleMedicineBinding.endDate.text =
                            "${
                                if (dayOfMonth + 1 < 10) {
                                    "0$dayOfMonth}"
                                } else {
                                    dayOfMonth
                                }
                            }" + "-" + if (monthOfYear + 1 < 10) {
                                "0${(monthOfYear + 1)}"
                            } else {
                                (monthOfYear + 1)
                            } + "-" + year
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
                        var dateFormat = SimpleDateFormat("dd-MM-yyyy")
                        val formatedDate: Date = dateFormat.parse(
                            fragmentScheduleMedicineBinding.endDate.text.toString().trim()
                        )!!
                        dateFormat = SimpleDateFormat("yyyy-MM-dd")
                        endDate = dateFormat.format(formatedDate)
                    }
                    if (addedMedication != null) {
                        val scheduledMedication = UpdateScheduledMedList(
                            doseID!!,
                            frequencyId!!.toString(),
                            daysIds!!,
                            timeAddedList,
                            fragmentScheduleMedicineBinding.etNote.text.toString().trim(),
                            endDate
                        )
                        medicationViewModel.updateScheduledMedication(
                            scheduledMedication,
                            addedMedication!!.id!!
                        )
                    } else if (medicationPayload != null) {
                        val scheduledMedication = UpdateScheduledMedList(
                            doseID!!,
                            frequencyId!!.toString(),
                            daysIds!!,
                            timeAddedList,
                            fragmentScheduleMedicineBinding.etNote.text.toString().trim(),
                            endDate
                        )
                        medicationViewModel.updateScheduledMedication(
                            scheduledMedication,
                            medicationPayload!!.id!!
                        )
                    } else {
                        val scheduledMedication =
                            ScheduledMedicationRequestModel(
                                medicationViewModel.getLovedOneUUId(),
                                doseID!!,
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
            R.id.doseTV -> {
                showDoseView()
            }
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
                timeList[position].time = String.format("%02d:%02d", hourOfDay, selectedMinute)
                if (hourOfDay < 12) {
                    timeList[position].time = String.format("%02d:%02d", hourOfDay, selectedMinute)
                    timeList[position].isAmPM = "am"
                } else {
                    timeList[position].time =
                        String.format("%02d:%02d", hourOfDay - 12, selectedMinute)
                    timeList[position].isAmPM = "pm"
                }
                timeAdapter!!.notifyDataSetChanged()
            }, hour,
            minute, false
        )
        mTimePicker.show()
    }

    private val isValid: Boolean
        get() {
            when {
                doseID == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_dose_for_medication)
                    )
                }
                frequencyId == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_frequcny_for_dose)
                    )
                }
                timeListCheck() -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_timing_all_medication)
                    )
                }
                daysIds == null -> {
                    showError(
                        requireContext(),
                        getString(R.string.please_select_days_for_medication)
                    )
                }
                fragmentScheduleMedicineBinding.etNote.text.toString().trim().isEmpty() -> {
                    fragmentScheduleMedicineBinding.etNote.error =
                        getString(R.string.please_enter_med_list_notes)
                    fragmentScheduleMedicineBinding.etNote.requestFocus()
                }

                else -> {
                    return true
                }
            }
            return false
        }

    private fun timeListCheck(): Boolean {
        var allfiled = false

        for (i in timeList) {
            if (i.time.isNullOrEmpty()) {
                allfiled = true
                break
            }
        }
        return allfiled
    }

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
        val df1: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        var date1: Date? = null
        var date2: Date? = null
        try {
            date1 = df1.parse(dateString1)
            date2 = df1.parse(dateString2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2)) {
            val id = when (SimpleDateFormat("EEE").format(cal1.time)) {
                "Mon" -> "1"
                "Tue" -> "2"
                "Wed" -> "3"
                "Thu" -> "4"
                "Fri" -> "5"
                "Sat" -> "6"
                else -> "7"
            }
            dates.add(DayList(id.toInt(), SimpleDateFormat("EEEE").format(cal1.time), false))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }

}