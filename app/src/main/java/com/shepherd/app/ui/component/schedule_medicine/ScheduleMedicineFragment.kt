package com.shepherd.app.ui.component.schedule_medicine

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.data.dto.med_list.schedule_medlist.DayList
import com.shepherd.app.data.dto.med_list.schedule_medlist.DoseList
import com.shepherd.app.data.dto.med_list.schedule_medlist.FrequencyData
import com.shepherd.app.data.dto.med_list.schedule_medlist.TimeSelectedlist
import com.shepherd.app.databinding.FragmentSchedulweMedicineBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.schedule_medicine.adapter.DaysAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.DoseAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.FrequencyAdapter
import com.shepherd.app.ui.component.schedule_medicine.adapter.TimeAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.AddMedicationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
@SuppressLint("NotifyDataSetChanged")
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
    private val medicationViewModel: AddMedicationViewModel by viewModels()
    var alDays =
        arrayOf(
            "Select Day",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
        )
    private val args: ScheduleMedicineFragmentArgs by navArgs()
    private var selectedMedList: Medlist? = null
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

    }

    override fun initViewBinding() {
        fragmentScheduleMedicineBinding.listener = this
        selectedMedList = args.medlist
        // set title of selected med
        fragmentScheduleMedicineBinding.tvMedTitle.text = selectedMedList?.name


        addFrequencyType()
        fragmentScheduleMedicineBinding.frequencyRV.adapter = FrequencyAdapter(
            requireContext(),
            this,
            frequencyList
        )

        timeList.add(TimeSelectedlist())
        addDays()
        setDayAdapter()
        setTimeAdapter()
        setDoseAdapter()

        medicationViewModel.getAllDoseList(pageNumber, limit)
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
        frequencyList.add(FrequencyData(frequencyList.size, "Once a day", 1))
        frequencyList.add(FrequencyData(frequencyList.size, "Twice a day", 2))
        frequencyList.add(FrequencyData(frequencyList.size, "Three times a day", 3))
        frequencyList.add(FrequencyData(frequencyList.size, "Four times a day", 4))
    }

    private fun addDays() {
        dayList.add(DayList(dayList.size + 1, "Monday", false))
        dayList.add(DayList(dayList.size + 1, "Tuesday", false))
        dayList.add(DayList(dayList.size + 1, "Wednesday", false))
        dayList.add(DayList(dayList.size + 1, "Thursday", false))
        dayList.add(DayList(dayList.size + 1, "Friday", false))
        dayList.add(DayList(dayList.size + 1, "Saturday", false))
        dayList.add(DayList(dayList.size + 1, "Sunday", false))
    }

    private fun selectedTime(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            timePicker(it)
        }
    }

    private fun selectedDay(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            dayList[it].isSelected = !dayList[it].isSelected
            dayAdapter!!.notifyDataSetChanged()
        }
    }

    private fun selectedDoseData(navigateEvent: SingleEvent<DoseList>) {
        navigateEvent.getContentIfNotHandled()?.let {
            //show selected dose
            selectedDose = it
            fragmentScheduleMedicineBinding.doseTV.text = selectedDose?.name ?: ""
            showDoseView()
            Log.e("catch_exception", "dose ${selectedDose?.name}")
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
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSubmit -> {
                if (isValid) {

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
                    timeList[position].isAmPM = "am"
                } else {
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
                fragmentScheduleMedicineBinding.tvTime.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_time_of_birth))
                    fragmentScheduleMedicineBinding.tvTime.requestFocus()
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

    override fun onSelected(position: Int) {
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