package com.shepherdapp.app.ui.component.addNewEvent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.animation.RotateAnimation
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.added_events.EventRecurringModel
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.databinding.FragmentAddNewEventBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addLovedOne.SearchPlacesActivity
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssigneAdapter
import com.shepherdapp.app.utils.RecurringEvent
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.hideKeyboard
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.utils.setupSnackbar
import com.shepherdapp.app.utils.showToast
import com.shepherdapp.app.view_model.AddNewEventViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Nikita Kohli on 26-04-22
 */
@AndroidEntryPoint
@SuppressLint("SimpleDateFormat", "SetTextI18n", "NotifyDataSetChanged")
class AddNewEventFragment : BaseFragment<FragmentAddNewEventBinding>(),
    View.OnClickListener, AssignToEventAdapter.selectedTeamMember,
    DatePickerDialog.OnDateSetListener {
    private lateinit var fragmentAddNewEventBinding: FragmentAddNewEventBinding
    private var assigneeAdapter: AssigneAdapter? = null
    private val addNewEventViewModel: AddNewEventViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var assignTo = ArrayList<String>()
    private var careTeams: MutableList<CareTeamModel> = ArrayList<CareTeamModel>()
    private var isAmPm: String? = null
    private var placeAddress: String? = null
    private var placeId: String? = null
    private var recurringValue: EventRecurringModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAddNewEventBinding =
            FragmentAddNewEventBinding.inflate(inflater, container, false)

        return fragmentAddNewEventBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initViewBinding() {
        fragmentAddNewEventBinding.listener = this

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        getAssignedToMembers()
        setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)
        fragmentAddNewEventBinding.etNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
        assigneeAdapter?.setHasStableIds(true)
        selectAllAssigneeCheckBoxListener()

        fragmentAddNewEventBinding.repeatCB.setOnClickListener {
            if (fragmentAddNewEventBinding.tvDate.text.toString().trim().isEmpty()) {
                showError(
                    requireContext(),
                    getString(R.string.please_select_new_care_point_date_firts)
                )
                fragmentAddNewEventBinding.tvDate.requestFocus()
                fragmentAddNewEventBinding.repeatCB.isChecked = false
            } else {
                showRepeatDialog(fragmentAddNewEventBinding.tvDate.text.toString())
            }
        }

        /*
                fragmentAddNewEventBinding.repeatCB.setOnCheckedChangeListener { viewPressed, isChecked ->
                    if (viewPressed.isPressed) {
                        showRepeatDialog()
                    }
                }
        */

    }

    private fun selectAllAssigneeCheckBoxListener() {
        fragmentAddNewEventBinding.tvSelect.setOnCheckedChangeListener { _, isChecked ->
            careTeams.forEachIndexed { position, _ ->

                careTeams[position].isSelected = isChecked
                val assignee: ArrayList<String> = arrayListOf()
                assignee.clear()
                for (i in careTeams) {
                    if (i.isSelected == true) {
                        assignee.add(
                            i.user_id_details?.firstname!!.plus(" ")
                                .plus(i.user_id_details?.lastname?.ifEmpty { null })
                        )
                    }
                }
                if (assignee.size > 0) {
                    fragmentAddNewEventBinding.assigneET.setText(assignee.joinToString())
                } else {
                    fragmentAddNewEventBinding.assigneET.setText("")
                }

            }
            fragmentAddNewEventBinding.assigneRV.postDelayed({
                assigneeAdapter!!.notifyDataSetChanged()
            }, 100)

        }

    }

    private fun getAssignedToMembers() {
        addNewEventViewModel.getMembers(
            pageNumber,
            limit,
            status,
            addNewEventViewModel.getLovedOneUUId()
        )
    }

    override fun observeViewModel() {
        observe(addNewEventViewModel.loginLiveData, ::handleLoginResult)
        observeSnackBarMessages(addNewEventViewModel.showSnackBar)
        observeToast(addNewEventViewModel.showToast)
        observeEventMembers()
        observeCreateEvent()
    }


    private fun observeEventMembers() {
        addNewEventViewModel.createEventLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(
                        requireContext(),
                        getString(R.string.new_care_point_added_successfully)
                    )
                    backPress()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    if (result.error.isNotEmpty()) {
                        showError(requireContext(), result.error)
                    } else {
                        result.message?.let { showError(requireContext(), it) }
                    }


                }
            }

        }
        addNewEventViewModel.eventMemberLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    val payload = result.data.payload
                    careTeams.addAll(payload.data)
                    careTeams = careTeams.filter {
                        it.permission?.contains("1")!!
                    }.toMutableList()
                    assigneeAdapter = AssigneAdapter(
                        this,
                        requireContext(),
                        careTeams
                    )
                    fragmentAddNewEventBinding.assigneRV.adapter = assigneeAdapter


                }

                is DataResult.Failure -> {
                    careTeams.add(CareTeamModel())
                    assigneeAdapter = AssigneAdapter(
                        this,
                        requireContext(),
                        careTeams
                    )
                    fragmentAddNewEventBinding.assigneRV.adapter = assigneeAdapter
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }

                }
            }
        }

    }

    private fun observeCreateEvent() {
        addNewEventViewModel.createEventLiveData.observeEvent(this) { result ->
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
                status.errorCode?.let { addNewEventViewModel.showToastMessage(it) }
            }
        }
    }

    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewEventBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        fragmentAddNewEventBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
    }

    private fun rotate(degree: Float) {
        val rotateAnim = RotateAnimation(
            0.0f, degree,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f
        )
        rotateAnim.duration = 0
        rotateAnim.fillAfter = true
        fragmentAddNewEventBinding.spinnerDownArrowImage.startAnimation(rotateAnim)
    }

    private var navLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 10101) onPlaceSelected(result.data)
        }

    private fun onPlaceSelected(data: Intent?) {
        placeAddress = data?.getStringExtra("placeName")
        placeId = data?.getStringExtra("placeId")
        fragmentAddNewEventBinding.edtAddress.setText(placeAddress)
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
                if (fragmentAddNewEventBinding.assigneRV.visibility == View.VISIBLE) {
                    fragmentAddNewEventBinding.assigneRV.visibility = View.GONE
                    fragmentAddNewEventBinding.tvSelect.visibility = View.GONE
                    rotate(0f)
                } else {
                    fragmentAddNewEventBinding.assigneRV.visibility = View.VISIBLE
                    fragmentAddNewEventBinding.tvSelect.visibility = View.VISIBLE
                    rotate(180f)
                }
            }

            R.id.btnAdd -> {
                assignTo.clear()
                for (i in careTeams) {
                    if (i.isSelected == true) {
                        assignTo.add(i.user_id_details?.uid!!)
                    }

                }
                if (isValid) {
                    createEvent()
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
        if (fragmentAddNewEventBinding.tvDate.text.toString().trim().isEmpty()) {
            showError(
                requireContext(),
                getString(R.string.please_select_new_care_point_date_firts)
            )
            fragmentAddNewEventBinding.tvDate.requestFocus()
        } else if (fragmentAddNewEventBinding.tvTime.text.toString().trim().isEmpty()) {
            timePicker()
        } else {
            val selectedDateTime =
                fragmentAddNewEventBinding.tvDate.text.toString().trim().plus(" ").plus(
                    fragmentAddNewEventBinding.tvTime.text.toString().trim().plus(" $amPm")
                )
            val currentDateTime =
                SimpleDateFormat("MM-dd-yyyy hh:mm a").format(Calendar.getInstance().time)

            val dateFormat = SimpleDateFormat("MM-dd-yyyy hh:mm a")
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
                fragmentAddNewEventBinding.tvDate.text =
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

                fragmentAddNewEventBinding.tvTime.text = ""
                isAmPm = null
                setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)

                // check for end date with recurring
                if (recurringValue != null && recurringValue!!.endDate != null) {
                    val recurringEndDate =
                        SimpleDateFormat("yyyy-MM-dd").parse(recurringValue!!.endDate!!)
                    val selectedDate =
                        SimpleDateFormat("MM/dd/yyyy").parse(fragmentAddNewEventBinding.tvDate.text.toString())
                    if (selectedDate!!.after(recurringEndDate)) {
                        fragmentAddNewEventBinding.repeatCB.isChecked = false
                        recurringValue!!.type = null
                        recurringValue!!.endDate = null
                        recurringValue!!.value = null
                        recurringValue!!.typeValue = null

                        fragmentAddNewEventBinding.txtType.isVisible = false
                        fragmentAddNewEventBinding.txtValue.isVisible = false
                        fragmentAddNewEventBinding.txtEndDate.isVisible = false
                    }
                }

            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentAddNewEventBinding.etEventName.text.toString().trim().isEmpty() -> {
                    fragmentAddNewEventBinding.etEventName.error =
                        getString(R.string.please_enter_event_name)
                    fragmentAddNewEventBinding.etEventName.requestFocus()
                }

                assignTo.size <= 0 -> {
                    showInfo(
                        requireContext(),
                        getString(R.string.please_select_whome_to_assign_event)
                    )
                }

                fragmentAddNewEventBinding.tvDate.text.toString().trim() == "DD/MM/YY" -> {
                    showInfo(requireContext(), getString(R.string.please_enter_date_of_event))
                    fragmentAddNewEventBinding.tvDate.requestFocus()
                }

                fragmentAddNewEventBinding.tvDate.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_date_of_event))
                    fragmentAddNewEventBinding.tvDate.requestFocus()
                }

                fragmentAddNewEventBinding.tvTime.text.toString().trim().isEmpty() -> {
                    showInfo(requireContext(), getString(R.string.please_enter_time_of_birth))
                    fragmentAddNewEventBinding.tvTime.requestFocus()
                }

                else -> {
                    return true
                }
            }
            return false
        }

    private fun createEvent() {
        var selectedDate = fragmentAddNewEventBinding.tvDate.text.toString().trim()
        var dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
        val formattedDate: Date = dateFormat.parse(
            selectedDate.plus(
                " ${
                    fragmentAddNewEventBinding.tvTime.text.toString().trim()
                } $isAmPm"
            )
        )!!
        dateFormat = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = dateFormat.format(formattedDate)
        dateFormat = SimpleDateFormat("hh:mm a")
        val selectedTime = dateFormat.format(formattedDate)

        val note = if (fragmentAddNewEventBinding.etNote.text.toString().isEmpty()) {
            null
        } else {
            fragmentAddNewEventBinding.etNote.text.toString().trim()
        }


        if (fragmentAddNewEventBinding.repeatCB.isChecked) {
            var dateWeekValue: ArrayList<Int>? = null
            var dateMonthValue: ArrayList<Int>? = null
            if (recurringValue != null && recurringValue!!.type == RecurringEvent.Weekly.value) {
                dateWeekValue = recurringValue!!.value
            } else if (recurringValue != null && recurringValue!!.type == RecurringEvent.Monthly.value) {
                dateMonthValue = recurringValue!!.value
            }
            if(dateWeekValue!=null){
                val hset: HashSet<Int> = HashSet<Int>(dateWeekValue)
                dateWeekValue.clear()
                dateWeekValue = arrayListOf()
                dateWeekValue.addAll(hset)
            }
            if(dateMonthValue!=null){
                val hset: HashSet<Int> = HashSet<Int>(dateMonthValue)
                dateMonthValue.clear()
                dateMonthValue = arrayListOf()
                dateMonthValue.addAll(hset)
            }
            val endDate = SimpleDateFormat("MM/dd/yyyy").parse(recurringValue?.endDate!!)
            val selectedEndDate = SimpleDateFormat("yyyy-MM-dd").format(endDate)
            addNewEventViewModel.createEvent(
                addNewEventViewModel.getLovedOneUUId(),
                fragmentAddNewEventBinding.etEventName.text.toString().trim(),
                fragmentAddNewEventBinding.edtAddress.text.toString().trim(),
                selectedDate,
                selectedTime,
                note,
                assignTo,
                recurringValue?.typeValue!!,
                selectedEndDate,
                dateWeekValue,
                dateMonthValue
            )
        } else {
            addNewEventViewModel.createEvent(
                addNewEventViewModel.getLovedOneUUId(),
                fragmentAddNewEventBinding.etEventName.text.toString().trim(),
                fragmentAddNewEventBinding.edtAddress.text.toString().trim(),
                selectedDate,
                selectedTime,
                note,
                assignTo, null, null, null, null
            )

        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }

    override fun onSelected(position: Int) {
        fragmentAddNewEventBinding.tvSelect.setOnCheckedChangeListener(null)
        careTeams[position].isSelected = !careTeams[position].isSelected!!
        fragmentAddNewEventBinding.assigneRV.postDelayed({
            assigneeAdapter!!.notifyDataSetChanged()
        }, 100)

        val assignee: ArrayList<String> = arrayListOf()
        assignee.clear()
        for (i in careTeams) {
            if (i.isSelected == true) {
                assignee.add(
                    i.user_id_details?.firstname!!.plus(" ")
                        .plus(i.user_id_details?.lastname?.ifEmpty { null })
                )
            }
        }

        if (assignee.size > 0) {
            fragmentAddNewEventBinding.assigneET.setText(assignee.joinToString())
        } else {
            fragmentAddNewEventBinding.assigneET.setText("")
        }

        var isAllChecked = true
        careTeams.forEach {
            if (it.isSelected == false) {
                isAllChecked = false
                return@forEach
            }
        }

        fragmentAddNewEventBinding.tvSelect.isChecked = isAllChecked

        selectAllAssigneeCheckBoxListener()
    }

    private fun timePicker() {
        if (fragmentAddNewEventBinding.tvDate.text.toString().trim().isEmpty()) {
            showError(requireContext(), getString(R.string.please_select_new_care_point_date_firts))
            fragmentAddNewEventBinding.tvDate.requestFocus()
        } else {
            val mCurrentTime = Calendar.getInstance()
            if (fragmentAddNewEventBinding.tvTime.text.isNotEmpty()) {
                val dateTime = fragmentAddNewEventBinding.tvDate.text.toString().trim().plus(" ")
                    .plus(fragmentAddNewEventBinding.tvTime.text.toString().plus(" $isAmPm"))
                mCurrentTime.time = SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(dateTime)!!
            }
            var hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            var minute = mCurrentTime.get(Calendar.MINUTE)

            val mTimePicker = TimePickerDialog(
                context, R.style.datepicker,
                { _, hourOfDay, selectedMinutes ->
                    //check event time for future events  only
                    val selectedDateTime =
                        fragmentAddNewEventBinding.tvDate.text.toString().trim().plus(" ")
                            .plus(String.format("%02d:%02d", hourOfDay, selectedMinutes))
                    val currentDateTime =
                        SimpleDateFormat("MM/dd/yyyy HH:mm").format(Calendar.getInstance().time)

                    val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")
                    if (dateFormat.parse(selectedDateTime)!!
                            .after(dateFormat.parse(currentDateTime))
                    ) {
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

                        val min =
                            if (minute.toString().length < 2) "0$minute" else java.lang.String.valueOf(
                                minute
                            )

                        val hours =
                            if (hour.toString().length < 2) "0$hour" else java.lang.String.valueOf(
                                hour
                            )

                        val mTime = StringBuilder().append(hours).append(':')
                            .append(min)

                        fragmentAddNewEventBinding.tvTime.text = mTime
                    } else {
                        showError(requireContext(), getString(R.string.please_select_future_time))
                    }

                }, hour,
                minute, false
            )
            mTimePicker.show()
        }
    }

    private fun setColorTimePicked(selected: Int, unselected: Int) {
        fragmentAddNewEventBinding.tvam.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                selected
            )
        )
        fragmentAddNewEventBinding.tvpm.setTextColor(
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
        fragmentAddNewEventBinding.tvDate.text = selectedDate
    }

    fun showEventEndDate(value: EventRecurringModel, days: String? = null) {
        recurringValue = value
        fragmentAddNewEventBinding.repeatCB.isChecked = true
        fragmentAddNewEventBinding.txtType.isVisible = false
        fragmentAddNewEventBinding.txtValue.isVisible = false
        fragmentAddNewEventBinding.txtEndDate.isVisible = false
        when (value.type) {
            RecurringEvent.None.value -> {
                fragmentAddNewEventBinding.repeatCB.isChecked = false
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
        fragmentAddNewEventBinding.txtType.isVisible = true
        fragmentAddNewEventBinding.txtValue.isVisible = valueBoolean
        fragmentAddNewEventBinding.txtEndDate.isVisible = true
        fragmentAddNewEventBinding.repeatCB.isChecked = true
        val dateSelected = SimpleDateFormat("MM/dd/yyyy").parse(value.endDate!!)
        val endDate = dateSelected?.let { SimpleDateFormat("EEE, MMM dd, yyyy").format(it) }
        fragmentAddNewEventBinding.txtEndDate.text = "Ends on - $endDate"
        if (value.value != null) {
            value.value!!.sort()
        }
        fragmentAddNewEventBinding.txtValue.text = value.value?.joinToString()
        when (value.type) {
            RecurringEvent.None.value -> {
                fragmentAddNewEventBinding.txtType.text = "None"
                fragmentAddNewEventBinding.repeatCB.isChecked = false
            }

            RecurringEvent.Daily.value -> {
                fragmentAddNewEventBinding.txtType.text = "Every Day"
                fragmentAddNewEventBinding.txtValue.isVisible = false
            }

            RecurringEvent.Weekly.value -> {
                fragmentAddNewEventBinding.txtType.text = "Every Week"
                fragmentAddNewEventBinding.txtValue.text = days
            }

            RecurringEvent.Monthly.value -> {
                fragmentAddNewEventBinding.txtType.text = "Every Month"
            }

            else -> {
                fragmentAddNewEventBinding.txtType.text = "None"
                fragmentAddNewEventBinding.repeatCB.isChecked = false
            }
        }

    }
}



