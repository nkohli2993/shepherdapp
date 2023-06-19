package com.shepherdapp.app.ui.component.edit_care_point

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.RotateAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.MonthModel
import com.shepherdapp.app.data.dto.WeekDataModel
import com.shepherdapp.app.data.dto.added_events.AddedEventModel
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.edit_event.EditEventRequestModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.databinding.FragmentEditCarePointBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addLovedOne.SearchPlacesActivity
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssigneAdapter
import com.shepherdapp.app.ui.component.addNewEvent.adapter.MonthAdapter
import com.shepherdapp.app.ui.component.carePoints.adapter.WeekAdapter
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.changeDatesFormat
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.EditEventViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Deepak Rattan on 07-12-22
 */
@AndroidEntryPoint
@SuppressLint("SimpleDateFormat", "SetTextI18n", "NotifyDataSetChanged")
class EditCarePointFragment : BaseFragment<FragmentEditCarePointBinding>(),
    View.OnClickListener, AssignToEventAdapter.selectedTeamMember,
    DatePickerDialog.OnDateSetListener {
    private var carePoint: AddedEventModel? = null
    private lateinit var fragmentEditCarePointBinding: FragmentEditCarePointBinding
    private var assigneeAdapter: AssigneAdapter? = null
    private val editEventViewModel: EditEventViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var assignTo = ArrayList<String>()
    private var careteams: MutableList<CareTeamModel> = ArrayList()
    private var isAmPm: String? = null
    private var placeAddress: String? = null
    private var placeId: String? = null
    private val TAG = "EditCarePointFragment"
    private val args: EditCarePointFragmentArgs by navArgs()
    private var oldAssignee: ArrayList<String> = arrayListOf()
    private var newAssigneesUUIDList: ArrayList<String> = arrayListOf()
    private var selectedAssigneeUUIDList: ArrayList<String> = arrayListOf()
    private var oldAssigneeUUIDList: ArrayList<String> = arrayListOf()
    private var list: ArrayList<String> = arrayListOf()
    private var deletedAssigneeUUIDList: ArrayList<String> = arrayListOf()
    private var recurringValue: EventRecurringModel? = null

    lateinit var monthAdapter: MonthAdapter
    lateinit var weekAdapter: WeekAdapter
    var selectedDatePickerDate = ""
    var radioGroupCheckId = 0
    var selectedMonthDates: ArrayList<MonthModel> = ArrayList()
    var selectedWeekDays: ArrayList<WeekDataModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentEditCarePointBinding =
            FragmentEditCarePointBinding.inflate(inflater, container, false)

        return fragmentEditCarePointBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() {
        fragmentEditCarePointBinding.listener = this

        carePoint = args.carePoint

        //Set EventName
        if (!carePoint?.name.isNullOrEmpty()) {
            fragmentEditCarePointBinding.etEventName.setText(carePoint?.name)
        }

        // Set address
        if (!carePoint?.location.isNullOrEmpty()) {
            fragmentEditCarePointBinding.edtAddress.setText(carePoint?.location)
        }

        // Set Assignee
        if (!carePoint?.user_assignes.isNullOrEmpty()) {
            carePoint?.user_assignes?.forEach {
                var name: String? = null
                val firstName = it.user_details.firstname.plus(" ")
                name = if (!it.user_details.lastname.isNullOrEmpty()) {
                    val lastName = it.user_details.lastname
                    firstName.plus(" ").plus(lastName)
                } else {
                    firstName
                }
                oldAssignee.add(name)
            }

            oldAssigneeUUIDList = carePoint?.user_assignes?.map {
                it.user_id
            } as ArrayList<String>


            if (oldAssignee.size > 0) {
                if (oldAssignee.size == 1) {
                    fragmentEditCarePointBinding.assigneET.setText(oldAssignee.joinToString())
                } else {
                    oldAssignee = oldAssignee.distinct() as ArrayList<String>
                    fragmentEditCarePointBinding.assigneET.setText(oldAssignee.joinToString())
                }

            } else {
                fragmentEditCarePointBinding.assigneET.setText("")
            }
        }

        // Set Date
        if (!carePoint?.date.isNullOrEmpty()) {
            fragmentEditCarePointBinding.tvDate.text = carePoint?.date.changeDatesFormat(
                sourceFormat = "yyyy-MM-dd",
                targetFormat = "MM/dd/yyyy"
            )
        }

        // Set Time
        if (carePoint?.time != null) {
            val carePointDate =
                if (carePoint!!.time!!.contains("am") || carePoint!!.time!!.contains("AM") || carePoint!!.time!!.contains(
                        "pm"
                    ) || carePoint?.time!!.contains("PM")
                ) {
                    SimpleDateFormat("yyyy-MM-dd hh:mm a").parse(
                        carePoint!!.date.plus(" ").plus(carePoint!!.time)
                    )
                } else {
                    SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                        carePoint!!.date.plus(" ").plus(carePoint!!.time)
                    )
                }

            val time = SimpleDateFormat("hh:mm a").format(carePointDate!!)
            Log.d(TAG, "initViewBinding: time is $time")
            if (time.contains("am") || time.contains("AM")) {
                isAmPm = "am"
                setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
            } else {
                isAmPm = "pm"
                setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
            }
            fragmentEditCarePointBinding.tvTime.text = time.dropLast(3)
        } else {
            setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)
        }

        if (!carePoint?.notes.isNullOrEmpty()) {
            // Set Note
            fragmentEditCarePointBinding.etNote.setText(carePoint?.notes)
        }

        Log.d(TAG, "initViewBinding: carePoint is $carePoint")

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getAssignedToMembers()
        fragmentEditCarePointBinding.etNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
        assigneeAdapter?.setHasStableIds(true)
        fragmentEditCarePointBinding.repeatCB.isChecked = false
        if (carePoint!!.repeat_flag != null) {
            fragmentEditCarePointBinding.repeatCB.isChecked = true
            fragmentEditCarePointBinding.txtType.isVisible = true
            fragmentEditCarePointBinding.txtValue.isVisible = true
            fragmentEditCarePointBinding.txtEndDate.isVisible = true

            when (carePoint!!.repeat_flag) {
                RecurringFlag.Daily.value -> {
                    fragmentEditCarePointBinding.txtType.text = getString(R.string.every_day)
                    fragmentEditCarePointBinding.txtValue.isVisible = false
                }

                RecurringFlag.Weekly.value -> {
                    fragmentEditCarePointBinding.txtType.text = getString(R.string.every_week)
                    if (carePoint!!.week_days != null) {
                        val daysList: ArrayList<String> = arrayListOf()
                        val weekArray = resources.getStringArray(R.array.week_array)
                        val weekAry: ArrayList<WeekDataModel> = arrayListOf()
                        for (i in weekArray.indices) {
                            weekAry.add(WeekDataModel((i + 1), weekArray[i]))
                        }
                        Log.e("catch_exception","list: ${weekAry.size}")
                        weekAry.add(WeekDataModel(weekAry.size + 1, "Sun"))

                        for (i in carePoint!!.week_days!!) {
                            for (weekId in weekAry) {
                                if (i == weekId.id) {
                                    daysList.add(weekId.name!!)
                                    break
                                }
                            }
                        }
                        fragmentEditCarePointBinding.txtValue.text = daysList.joinToString()
                    }
                }

                RecurringFlag.Monthly.value -> {

                    var selectedDates = ""
                    val selectedMonthListName: ArrayList<Int> = arrayListOf()

                    selectedMonthDates.forEach {
                        selectedMonthListName.add(it.monthDate.toInt())
                    }

                    selectedMonthListName.sort()
                    selectedMonthListName.forEach {
                        selectedDates = if (selectedDates.isNotEmpty())
                            "$selectedDates,$it"
                        else
                            it.toString()
                    }

                    fragmentEditCarePointBinding.txtType.text = getString(R.string.every_month)

                    if (selectedDates.isNullOrEmpty())
                        fragmentEditCarePointBinding.txtValue.text =
                            carePoint!!.month_dates?.joinToString()
                    else
                    fragmentEditCarePointBinding.txtValue.text =
                        selectedDates
                }

                else -> {

                }
            }
            val dateSelected =
                SimpleDateFormat("yyyy-MM-dd").parse(carePoint!!.repeat_end_date!!)
            val endDate =
                dateSelected?.let { SimpleDateFormat("EEE, MMM dd, yyyy").format(it) }
            fragmentEditCarePointBinding.txtEndDate.text = "Ends on - $endDate"

        }


        fragmentEditCarePointBinding.repeatCB.setOnClickListener {
            if (carePoint!!.repeat_flag != null) {
                fragmentEditCarePointBinding.repeatCB.isChecked = true
            }
            showRepeatDialog(carePoint!!)
        }

        selectAllAssigneeCheckBoxListener()
    }

    private fun selectAllAssigneeCheckBoxListener() {
        fragmentEditCarePointBinding.tvSelect.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                careteams.forEachIndexed { position, _ ->
                    careteams[position].isSelected = isChecked
                    val assignee: ArrayList<String> = arrayListOf()
                    assignee.clear()
                    selectedAssigneeUUIDList.clear()
                    for (i in careteams) {
                        if (i.isSelected == true) {
                            val uuid = i.user_id_details?.uid
                            uuid?.let { selectedAssigneeUUIDList.add(it) }
                            assignee.add(
                                i.user_id_details?.firstname!!.plus(" ")
                                    .plus(i.user_id_details?.lastname?.ifEmpty { null })
                            )
                        }
                    }
                    if (assignee.size > 0) {
                        fragmentEditCarePointBinding.assigneET.setText(assignee.joinToString())
                    } else {
                        fragmentEditCarePointBinding.assigneET.setText("")
                    }
                }
                fragmentEditCarePointBinding.assigneRV.postDelayed({
                    assigneeAdapter!!.notifyDataSetChanged()
                }, 100)
            }
        }


    }

    private fun getAssignedToMembers() {
        editEventViewModel.getMembers(
            pageNumber,
            limit,
            status,
            editEventViewModel.getLovedOneUUId()
        )
    }

    override fun observeViewModel() {
        observe(editEventViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(editEventViewModel.showSnackBar)
        observeToast(editEventViewModel.showToast)
        observeEventMembers()
        observeCreateEvent()
    }


    private fun observeEventMembers() {

        editEventViewModel.eventMemberLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
//                    fragmentEditCarePointBinding.tvSelect.setOnCheckedChangeListener(null)

                    val payload = result.data.payload
                    careteams.addAll(payload.data)

                    careteams = careteams.filter {
                        it.permission?.contains(Modules.CarePoints.value)!!
                    }.toMutableList()


                    assigneeAdapter = AssigneAdapter(
                        this,
                        requireContext(),
                        careteams
                    )
                    fragmentEditCarePointBinding.assigneRV.adapter = assigneeAdapter

                    args.carePoint?.user_assignes?.forEach { assignee ->
                        careteams.forEach {
                            if (it.user_id == assignee.user_id) {
                                it.isSelected = true
                            }
                        }
                    }

                    assigneeAdapter!!.setData(careteams)


                    var isAllChecked = true
                    careteams.forEach {
                        if (it.isSelected == false) {
                            isAllChecked = false
                            return@forEach
                        }
                    }

                    fragmentEditCarePointBinding.tvSelect.isChecked = isAllChecked
                }

                is DataResult.Failure -> {
                    careteams.add(CareTeamModel())
                    assigneeAdapter = AssigneAdapter(
                        this,
                        requireContext(),
                        careteams
                    )
                    fragmentEditCarePointBinding.assigneRV.adapter = assigneeAdapter
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }

                }
            }
        }

        // Observe edit care point response
        editEventViewModel.editCarePointResponseLiveData.observeEvent(this) {
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
//                    showSuccess(requireContext(), it.data.message.toString())
                    showSuccess(requireContext(), "CarePoint updated successfully")
                    backPress()
                }
            }
        }

    }

    private fun observeCreateEvent() {
        editEventViewModel.createEventLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }

                }
            }
        }
    }

    private fun handleLoginResult(status: Resource<LoginResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }

            is Resource.DataError -> {
                status.errorCode?.let { editEventViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentEditCarePointBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentEditCarePointBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun rotate(degree: Float) {
        val rotateAnim = RotateAnimation(
            0.0f, degree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 0
        rotateAnim.fillAfter = true
        fragmentEditCarePointBinding.spinnerDownArrowImage.startAnimation(rotateAnim)
    }

    private var navLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 10101) onPlaceSelected(result.data)
        }

    private fun onPlaceSelected(data: Intent?) {
        placeAddress = data?.getStringExtra("placeName")
        placeId = data?.getStringExtra("placeId")
        fragmentEditCarePointBinding.edtAddress.setText(placeAddress)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.edtAddress -> {
                val intent = Intent(requireContext(), SearchPlacesActivity::class.java)
                intent.putExtra("search_type", "event")
                navLauncher.launch(intent)
            }

            R.id.ivBack -> {
                findNavController().popBackStack()
            }

            R.id.assigneET, R.id.spinner_down_arrow_image -> {
                hideKeyboard()
                if (fragmentEditCarePointBinding.assigneRV.visibility == View.VISIBLE) {
                    fragmentEditCarePointBinding.assigneRV.visibility = View.GONE
                    fragmentEditCarePointBinding.tvSelect.visibility = View.GONE
                    rotate(0f)
                } else {
                    fragmentEditCarePointBinding.assigneRV.visibility = View.VISIBLE
                    fragmentEditCarePointBinding.tvSelect.visibility = View.VISIBLE
                    rotate(180f)
                }
            }

            R.id.btnSaveChanges -> {
                assignTo.clear()
                newAssigneesUUIDList.clear()
                deletedAssigneeUUIDList.clear()
                for (i in careteams) {
                    if (i.isSelected == true) {
                        assignTo.add(i.user_id_details?.uid!!)
                    }
                }
                newAssigneesUUIDList = assignTo.filter {
                    it !in oldAssigneeUUIDList
                } as ArrayList<String>

                Log.d(TAG, " New Assignees : $newAssigneesUUIDList")

                deletedAssigneeUUIDList = oldAssigneeUUIDList.filter {
                    it !in assignTo
                } as ArrayList<String>

                Log.d(TAG, "Deleted Assignees: $deletedAssigneeUUIDList")

                if (isValid) {
                    editEvent()
                }
            }

            R.id.clTimeWrapper, R.id.tvTime -> {
                timePicker()
            }

            R.id.tvam -> {
                changeTimeAmPm("am")
            }

            R.id.tvpm -> {
                changeTimeAmPm("pm")
            }

            R.id.tvDate -> {
                datePicker()
            }
        }
    }

    private fun changeTimeAmPm(amPm: String) {
        if (fragmentEditCarePointBinding.tvDate.text.toString().trim().isEmpty()) {
            showError(
                requireContext(),
                getString(R.string.please_select_new_care_point_date_firts)
            )
            fragmentEditCarePointBinding.tvDate.requestFocus()
        } else if (fragmentEditCarePointBinding.tvTime.text.toString().trim().isEmpty()) {
            timePicker()
        } else {
            val selectedDateTime =
                fragmentEditCarePointBinding.tvDate.text.toString().trim().plus(" ").plus(
                    fragmentEditCarePointBinding.tvTime.text.toString().trim().plus(" $amPm")
                )
            val currentDateTime =
                SimpleDateFormat("MM/dd/yyyy hh:mm a").format(Calendar.getInstance().time)

            val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
            if (dateFormat.parse(selectedDateTime)!!
                    .after(dateFormat.parse(currentDateTime))
            ) {
                if (amPm == "am") {
                    isAmPm = "am"
                    setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                } else {
                    isAmPm = "pm"
                    setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                }

            } else {
                showError(requireContext(), getString(R.string.please_select_future_time))
            }
        }
    }


    private fun datePicker() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            requireActivity(), R.style.datepicker,
            { _, year, monthOfYear, dayOfMonth ->
                fragmentEditCarePointBinding.tvDate.text =
                    "${
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

                fragmentEditCarePointBinding.tvTime.text = ""
                isAmPm = null
                setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)

                // check for end date with recurring
                if (carePoint!!.repeat_end_date != null) {
                    val recurringEndDate =
                        SimpleDateFormat("yyyy-MM-dd").parse(carePoint!!.repeat_end_date!!)
                    val selectedDate =
                        SimpleDateFormat("MM/dd/yyyy").parse(fragmentEditCarePointBinding.tvDate.text.toString())
                    if (selectedDate!!.after(recurringEndDate)) {
                        fragmentEditCarePointBinding.repeatCB.isChecked = false
                        carePoint!!.repeat_flag = null
                        carePoint!!.repeat_end_date = null
                        carePoint!!.week_days = null
                        carePoint!!.month_dates = null

                        fragmentEditCarePointBinding.txtType.isVisible = false
                        fragmentEditCarePointBinding.txtValue.isVisible = false
                        fragmentEditCarePointBinding.txtEndDate.isVisible = false
                    }
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    private val isValid: Boolean
        get() {
            var eventDate: Date? = null
            var currentDate: Date? = null
            /*
                        if (fragmentEditCarePointBinding.tvDate.text.toString().trim()
                                .isNotEmpty() && fragmentEditCarePointBinding.tvTime.text.toString().trim()
                                .isNotEmpty()
                        ) {
                            val df = SimpleDateFormat(
                                "MM-dd-yyyy hh:mm a",
                                Locale.getDefault()
                            ) // pass the format pattern that you like and done.
                            eventDate = df.parse(
                                fragmentEditCarePointBinding.tvDate.text.toString().trim() + " " +
                                        fragmentEditCarePointBinding.tvTime.text.toString()
                                            .trim() + " " + isAmPm
                            )
                            currentDate = df.parse(df.format(Date()))


                        }
            */

            when {
                fragmentEditCarePointBinding.etEventName.text.toString().trim().isEmpty() -> {
                    fragmentEditCarePointBinding.etEventName.error =
                        getString(R.string.please_enter_event_name)
                    fragmentEditCarePointBinding.etEventName.requestFocus()
                }

                assignTo.size <= 0 -> {
                    if (oldAssigneeUUIDList.isEmpty()) {
                        showInfo(
                            requireContext(),
                            getString(R.string.please_select_whome_to_assign_event)
                        )
                    } else {
                        return true
                    }
                }

                fragmentEditCarePointBinding.tvDate.text.toString().trim() == "DD/MM/YY" -> {
                    showInfo(requireContext(), getString(R.string.please_enter_date_of_event))
                    fragmentEditCarePointBinding.tvDate.requestFocus()
                }

                fragmentEditCarePointBinding.tvDate.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_date_of_event))
                    fragmentEditCarePointBinding.tvDate.requestFocus()
                }

                fragmentEditCarePointBinding.tvTime.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_time_of_birth))
                    fragmentEditCarePointBinding.tvTime.requestFocus()
                }

                /* eventDate?.before(currentDate)!! -> {
                     showInfo(requireContext(), getString(R.string.please_select_fututre_date))
                     fragmentEditCarePointBinding.tvDate.requestFocus()
                 }*/

                else -> {
                    return true
                }
            }
            return false
        }

    private fun editEvent() {
        var selectedDate = fragmentEditCarePointBinding.tvDate.text.toString().trim()
        var dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
        val formattedDate: Date = dateFormat.parse(
            selectedDate.plus(
                " ${
                    fragmentEditCarePointBinding.tvTime.text.toString().trim()
                } $isAmPm"
            )
        )!!
        dateFormat = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = dateFormat.format(formattedDate)
        dateFormat = SimpleDateFormat("hh:mm a")
        val selectedTime = dateFormat.format(formattedDate)

        val note = if (fragmentEditCarePointBinding.etNote.text.toString().isEmpty()) {
            null
        } else {
            fragmentEditCarePointBinding.etNote.text.toString().trim()
        }

        val location =
            if (fragmentEditCarePointBinding.edtAddress.text.toString().trim().isEmpty()) {
                null
            } else {
                fragmentEditCarePointBinding.edtAddress.text.toString().trim()
            }


        if (fragmentEditCarePointBinding.repeatCB.isChecked) {
            var dateWeekValue: ArrayList<Int>? = null
            var dateMonthValue: ArrayList<Int>? = null
            var selectedMonthListString: ArrayList<Int>? = null
            if (carePoint != null && carePoint!!.repeat_flag == RecurringFlag.Weekly.value) {
                dateWeekValue = carePoint!!.week_days
                // add check for start date
                if (dateWeekValue != null) {
                    dateWeekValue.sort()
                    val now = Calendar.getInstance()
                    val weekday = now[Calendar.DAY_OF_WEEK]
                    if (weekday != (dateWeekValue[0] + 1)) {
                        val days = (Calendar.SATURDAY - weekday + dateWeekValue[0] + 1) % 7
                        now.add(Calendar.DAY_OF_YEAR, days)
                    }
                    val dateStart = now.time
                    val format = SimpleDateFormat("yyyy-MM-dd").format(dateStart)
                    Log.e("catch_exception", "date: $format $selectedDate")
                    val getstartDate = SimpleDateFormat("yyyy-MM-dd").parse(format)
                    val selectedStartDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)

                    if (selectedStartDate.before(getstartDate)) {
                        selectedDate = format
                    }

                    Log.e("catch_exception", "startDate: $selectedDate")
                }



            } else if (carePoint != null && carePoint!!.repeat_flag == RecurringFlag.Monthly.value) {
                dateMonthValue = recurringValue!!.value
                selectedMonthListString = arrayListOf()
                selectedMonthDates.forEach {
                    selectedMonthListString.add(it.monthDate.toInt())
                }

                selectedMonthListString.sort()
                // add check for start date
                if (selectedMonthListString != null) {
                    selectedMonthListString.sort()
                    val now = Calendar.getInstance()
                    val dateStart = now.time
                    val format = SimpleDateFormat("yyyy-MM").format(dateStart)
                    Log.e("catch_exception", "date: ${format.plus("-${selectedMonthListString[0]}")} $selectedDate")
                    val getstartDate = SimpleDateFormat("yyyy-MM-dd").parse(format.plus("-${selectedMonthListString[0]}"))
                    val selectedStartDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)
                    selectedDate = format.plus("-${selectedMonthListString[0]}")
//                    if (selectedStartDate.before(getstartDate)) {
//                        selectedDate = format.plus("-${selectedMonthListString[0]}")
//                    }

                    Log.e("catch_exception", "startDate: $selectedDate")
                }


            }
            if (dateWeekValue != null) {
                val hset: HashSet<Int> = HashSet<Int>(dateWeekValue)
                dateWeekValue.clear()
                dateWeekValue = arrayListOf()
                dateWeekValue.addAll(hset)
            }
            if (dateMonthValue != null) {
                val hset: HashSet<Int> = HashSet<Int>(dateMonthValue)
                dateMonthValue.clear()
                dateMonthValue = arrayListOf()
                dateMonthValue.addAll(hset)
            }
            val endDate = SimpleDateFormat("yyyy-MM-dd").parse(carePoint!!.repeat_end_date!!)
            val selectedEndDate = SimpleDateFormat("yyyy-MM-dd").format(endDate!!)





            carePoint?.id?.let {
                editEventViewModel.editCarePoint(
                    EditEventRequestModel(
                        name = fragmentEditCarePointBinding.etEventName.text.toString().trim(),
                        location = location,
                        date = selectedDate,
                        time = selectedTime,
                        notes = note,
                        deletedAssignee = deletedAssigneeUUIDList,
                        newAssignee = newAssigneesUUIDList,
                        repeat_flag = carePoint!!.repeat_flag,
                        repeat_end_date = selectedEndDate,
                        week_days = dateWeekValue,
                        month_dates = selectedMonthListString
                    ),
                    it
                )
            }

        } else {
            carePoint?.id?.let {
                editEventViewModel.editCarePoint(
                    EditEventRequestModel(
                        name = fragmentEditCarePointBinding.etEventName.text.toString().trim(),
                        location = location,
                        date = selectedDate,
                        time = selectedTime,
                        notes = note,
                        deletedAssignee = deletedAssigneeUUIDList,
                        newAssignee = newAssigneesUUIDList
                    ),
                    it
                )
            }


        }

    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }

    override fun onSelected(position: Int) {
        fragmentEditCarePointBinding.tvSelect.setOnCheckedChangeListener(null)

        careteams[position].isSelected = !careteams[position].isSelected!!
        fragmentEditCarePointBinding.assigneRV.postDelayed({
            assigneeAdapter!!.notifyDataSetChanged()
        }, 100)
        val assignee: ArrayList<String> = arrayListOf()
        assignee.clear()
        selectedAssigneeUUIDList.clear()
        for (i in careteams) {
            if (i.isSelected == true) {
                val uuid = i.user_id_details?.uid
                uuid?.let { selectedAssigneeUUIDList.add(it) }
                assignee.add(
                    i.user_id_details?.firstname!!.plus(" ")
                        .plus(i.user_id_details?.lastname?.ifEmpty { null })
                )
            }
        }
        if (assignee.size > 0) {
            fragmentEditCarePointBinding.assigneET.setText(assignee.joinToString())
        } else {
            fragmentEditCarePointBinding.assigneET.setText("")
        }

        var isAllChecked = true
        careteams.forEach {
            if (it.isSelected == false) {
                isAllChecked = false
                return@forEach
            }
        }

        fragmentEditCarePointBinding.tvSelect.isChecked = isAllChecked
        selectAllAssigneeCheckBoxListener()
    }

    private fun timePicker() {
        if (fragmentEditCarePointBinding.tvDate.text.toString().trim().isEmpty()) {
            showError(requireContext(), getString(R.string.please_select_new_care_point_date_firts))
            fragmentEditCarePointBinding.tvDate.requestFocus()
        } else {
            val mCurrentTime = Calendar.getInstance()
            if (fragmentEditCarePointBinding.tvTime.text.isNotEmpty()) {

                val dateTime = fragmentEditCarePointBinding.tvDate.text.toString().trim().plus(" ")
                    .plus(
                        fragmentEditCarePointBinding.tvTime.text.toString().plus(" ")
                            .plus("$isAmPm")
                    )


                mCurrentTime.time = SimpleDateFormat("MM/dd/yyyy hh:mm a").parse(dateTime)!!
            }
            var hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = mCurrentTime.get(Calendar.MINUTE)

            val mTimePicker = TimePickerDialog(
                context, R.style.datepicker,
                { _, hourOfDay, selectedMinutes ->
                    //check event time for future events  only
                    val selectedDateTime =
                        fragmentEditCarePointBinding.tvDate.text.toString().trim().plus(" ")
                            .plus(String.format("%02d:%02d", hourOfDay, selectedMinutes))
                    val currentDateTime =
                        SimpleDateFormat("MM/dd/yyyy HH:mm").format(Calendar.getInstance().time)

                    val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")
                    val selDateTime = dateFormat.parse(selectedDateTime)
                    val curDateTime = dateFormat.parse(currentDateTime)
                    if (selDateTime?.after(curDateTime) == true) {
                        hour = hourOfDay
                        minute = selectedMinutes
                        isAmPm = ""
                        if (hour > 12) {
                            hour -= 12
                            isAmPm = "pm"
                            setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                        } else if (hour == 0) {
                            hour += 12
                            isAmPm = "am"
                            setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                        } else if (hour == 12) {
                            setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                            isAmPm = "pm"
                        } else {
                            isAmPm = "am"
                            setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                        }

                        val min: String =
                            if (minute.toString().length < 2) "0$minute" else java.lang.String.valueOf(
                                minute
                            )

                        val hours: String =
                            if (hour.toString().length < 2) "0$hour" else java.lang.String.valueOf(
                                hour
                            )


                        val mTime = StringBuilder().append(hours).append(':')
                            .append(min)

                        fragmentEditCarePointBinding.tvTime.text = mTime
                    } else {
                        showError(
                            requireContext(),
                            getString(R.string.please_select_future_time)
                        )
                    }

                }, hour,
                minute, false
            )
            mTimePicker.show()
        }
    }

    private fun setColorTimePicked(selected: Int, unselected: Int) {
        fragmentEditCarePointBinding.tvam.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                selected
            )
        )
        fragmentEditCarePointBinding.tvpm.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                unselected
            )
        )
    }


    override fun onDateSet(p0: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val selectedDate: String =
            SimpleDateFormat("dd/MMM/yyyy").format(mCalendar.time)
        fragmentEditCarePointBinding.tvDate.text = selectedDate
    }


    fun showEventEndDate(value: EventRecurringModel, days: String? = null) {
        recurringValue = value
        fragmentEditCarePointBinding.repeatCB.isChecked = true
        fragmentEditCarePointBinding.txtType.isVisible = false
        fragmentEditCarePointBinding.txtValue.isVisible = false
        fragmentEditCarePointBinding.txtEndDate.isVisible = false
        when (value.type) {
            RecurringEvent.None.value -> {
                fragmentEditCarePointBinding.repeatCB.isChecked = false
                fragmentEditCarePointBinding.txtType.isVisible = false
                fragmentEditCarePointBinding.txtEndDate.isVisible = false
                fragmentEditCarePointBinding.repeatCB.isChecked = false
            }

            RecurringEvent.Daily.value -> {
                visibleRecurringView(valueBoolean = false, value)
            }

            RecurringEvent.Weekly.value -> {
                visibleRecurringView(valueBoolean = true, value, days)
            }

            RecurringEvent.Monthly.value -> {
                visibleRecurringView(valueBoolean = true, value)
            }
        }
    }

    private fun visibleRecurringView(
        valueBoolean: Boolean,
        value: EventRecurringModel,
        days: String? = null
    ) {
        fragmentEditCarePointBinding.txtType.isVisible = true
        fragmentEditCarePointBinding.txtValue.isVisible = valueBoolean
        fragmentEditCarePointBinding.txtEndDate.isVisible = true
        fragmentEditCarePointBinding.repeatCB.isChecked = true
        val dateSelected = SimpleDateFormat("MM/dd/yyyy").parse(value.endDate!!)
        val endDate = dateSelected?.let { SimpleDateFormat("EEE, MMM dd, yyyy").format(it) }
        fragmentEditCarePointBinding.txtEndDate.text = "Ends on - $endDate"
        if (value.value != null) {
            value.value!!.sort()
        }
        var selectedDates = ""
        val selectedMonthListName: ArrayList<Int> = arrayListOf()

        selectedMonthDates.forEach {
            selectedMonthListName.add(it.monthDate.toInt())
        }

        selectedMonthListName.sort()
        selectedMonthListName.forEach {
            selectedDates = if (selectedDates.isNotEmpty())
                selectedDates + "," + it
            else
                it.toString()
        }

        if (selectedDates.isNullOrEmpty())
        fragmentEditCarePointBinding.txtValue.text = value.value?.joinToString()
        else
            fragmentEditCarePointBinding.txtValue.text = selectedDates


        val endDateSelected = SimpleDateFormat("MM/dd/yyyy").parse(recurringValue?.endDate!!)
        val selectedEndDate = SimpleDateFormat("yyyy-MM-dd").format(endDateSelected!!)
        carePoint!!.repeat_end_date = selectedEndDate
        when (value.type) {
            RecurringEvent.None.value -> {
                fragmentEditCarePointBinding.txtType.text = "None"
                fragmentEditCarePointBinding.repeatCB.isChecked = false
            }

            RecurringEvent.Daily.value -> {
                fragmentEditCarePointBinding.txtType.text = "Every Day"
                carePoint!!.repeat_flag = "day"
                fragmentEditCarePointBinding.txtValue.isVisible = false
            }

            RecurringEvent.Weekly.value -> {
                fragmentEditCarePointBinding.txtType.text = "Every Week"
                fragmentEditCarePointBinding.txtValue.text = days
                carePoint!!.repeat_flag = "week"
                carePoint!!.month_dates?.clear()
                carePoint!!.month_dates = arrayListOf()
                carePoint!!.week_days?.clear()
                carePoint!!.week_days = arrayListOf()
                carePoint!!.week_days?.addAll(value.value!!)
            }

            RecurringEvent.Monthly.value -> {
                fragmentEditCarePointBinding.txtType.text = "Every Month"
                carePoint!!.repeat_flag = "month"
                carePoint!!.week_days?.clear()
                carePoint!!.week_days = arrayListOf()
                carePoint!!.month_dates?.clear()
                carePoint!!.month_dates = arrayListOf()
                carePoint!!.month_dates?.addAll(value.value!!)
            }

            else -> {
                fragmentEditCarePointBinding.txtType.text = "None"
                fragmentEditCarePointBinding.repeatCB.isChecked = false
            }
        }

    }
}

