package com.shepherd.app.ui.component.schedule_medicine

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
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
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged,SetTextI18n")
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

                    if (doseList.isNullOrEmpty()) return@observeEvent
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
                    showInfo(
                        requireContext(),
                        getString(R.string.scheduled_medication_created_successfully)
                    )
                    hideLoading()
                    backPress()
                }
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
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

        addFrequencyType()
        fragmentScheduleMedicineBinding.frequencyRV.adapter = FrequencyAdapter(
            requireContext(),
            this,
            frequencyList
        )

        medicationViewModel.getAllDoseList(pageNumber, limit)

        //set data according to value added
        if (addedMedication != null) {
            val frequency = when (addedMedication?.frequency) {
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
            fragmentScheduleMedicineBinding.doseTV.text = addedMedication?.dosageId.toString()
            if (addedMedication?.endDate != null) {
                var selectedDate = addedMedication?.endDate
                val formatedDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)!!
                fragmentScheduleMedicineBinding.endDate.text =
                    SimpleDateFormat("dd-MM-yyyy").format(formatedDate)
            }
            timeList.clear()
          /*  for (i in addedMedication?.time!!) {
                timeList.add(TimeSelectedlist(timeList.size, i.time, i.hour!!.lowercase()))
            }*/
            timeList.add(TimeSelectedlist(timeList.size, addedMedication?.time!!.time, addedMedication?.time!!.hour!!.lowercase()))
            setTimeAdapter()
            addDays(isEdit = true, addedMedication?.days!!)
            setDayAdapter()
            fragmentScheduleMedicineBinding.etNote.setText(addedMedication?.note)
        } else {
            timeList.add(TimeSelectedlist())
            addDays()
            setDayAdapter()
            setTimeAdapter()
        }
        setDoseAdapter()
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

    private fun addDays(isEdit: Boolean = false, dayId: String = "") {
        dayList.add(DayList(dayList.size + 1, "Monday", false))
        dayList.add(DayList(dayList.size + 1, "Tuesday", false))
        dayList.add(DayList(dayList.size + 1, "Wednesday", false))
        dayList.add(DayList(dayList.size + 1, "Thursday", false))
        dayList.add(DayList(dayList.size + 1, "Friday", false))
        dayList.add(DayList(dayList.size + 1, "Saturday", false))
        dayList.add(DayList(dayList.size + 1, "Sunday", false))
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

    fun stringToWords(s: String) = s.trim().splitToSequence(',')
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
                                    "0${(dayOfMonth + 1)}"
                                } else {
                                    (dayOfMonth + 1)
                                }
                            }" + "-" + if (monthOfYear + 1 < 10) {
                                "0${(monthOfYear + 1)}"
                            } else {
                                (monthOfYear + 1)
                            } + "-" + year
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
                    var endDate = ""
                    if (fragmentScheduleMedicineBinding.endDate.text.toString().trim()
                            .isNotEmpty()
                    ) {
                        var selectedDate =
                            fragmentScheduleMedicineBinding.endDate.text.toString().trim()
                        var dateFormat = SimpleDateFormat("dd-MM-yyyy")
                        val formatedDate: Date = dateFormat.parse(selectedDate)!!
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
            { view, hourOfDay, selectedMinute ->
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
            minute, true
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

        timeAdapter?.removeData(list)
        timeList.clear()
        timeList.addAll(list)
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
    }

    private fun showDayView() {
        if (fragmentScheduleMedicineBinding.daysRV.visibility == View.VISIBLE) {
            fragmentScheduleMedicineBinding.daysRV.visibility = View.GONE
            rotate(0f, fragmentScheduleMedicineBinding.dayIM)
        } else {
            fragmentScheduleMedicineBinding.daysRV.visibility = View.VISIBLE
            rotate(180f, fragmentScheduleMedicineBinding.dayIM)
        }
    }

    private fun showDoseView() {
        if (fragmentScheduleMedicineBinding.doseRV.visibility == View.VISIBLE) {
            fragmentScheduleMedicineBinding.doseRV.visibility = View.GONE
            rotate(0f, fragmentScheduleMedicineBinding.doseIM)
        } else {
            fragmentScheduleMedicineBinding.doseRV.visibility = View.VISIBLE
            rotate(180f, fragmentScheduleMedicineBinding.doseIM)
        }
    }

}