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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.shepherdapp.app.R
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.databinding.FragmentEditCarePointBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addLovedOne.SearchPlacesActivity
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.shepherdapp.app.ui.component.addNewEvent.adapter.AssigneAdapter
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.utils.setupSnackbar
import com.shepherdapp.app.utils.showToast
import com.shepherdapp.app.view_model.AddNewEventViewModel
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
    private lateinit var fragmentEditCarePointBinding: FragmentEditCarePointBinding
    private var assigneeAdapter: AssigneAdapter? = null
    private val addNewEventViewModel: AddNewEventViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var assignTo = ArrayList<String>()
    private var careteams = ArrayList<CareTeamModel>()
    private var isAmPm: String? = null
    private var placeAddress: String? = null
    private var placeId: String? = null
    private val TAG = "EditCarePointFragment"
    private val args: EditCarePointFragmentArgs by navArgs()

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

        val carePoint = args.carePoint

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
            val assignee: ArrayList<String> = arrayListOf()

            carePoint?.user_assignes?.forEach {
                var name: String? = null
                val firstName = it.user_details.firstname.plus(" ")
                name = if (!it.user_details.lastname.isNullOrEmpty()) {
                    val lastName = it.user_details.lastname
                    firstName.plus(" ").plus(lastName)
                } else {
                    firstName
                }
                assignee.add(name)
            }

            if (assignee.size > 0) {
                fragmentEditCarePointBinding.assigneET.setText(assignee.joinToString())
            } else {
                fragmentEditCarePointBinding.assigneET.setText("")
            }
        }

        // Set Date
        if (!carePoint?.date.isNullOrEmpty()) {
            fragmentEditCarePointBinding.tvDate.text = carePoint?.date
        }

        // Set Time
        if (carePoint?.time != null) {
            val carePointDate = SimpleDateFormat("yyyy-MM-dd HH:mm").parse(
                carePoint.date.plus(" ").plus(carePoint.time?.replace(" ", ""))
            )
            val time = SimpleDateFormat("hh:mm a").format(carePointDate!!)
            Log.d(TAG, "initViewBinding: time is $time")
            if (time.contains("am")) {
                setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
            } else {
                setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
            }
            fragmentEditCarePointBinding.tvTime.text = time.dropLast(2)
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

        /*fragmentAddNewEventBinding.etNote.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                fragmentAddNewEventBinding.scrollView.postDelayed(Runnable {
                    val lastChild: View =
                        fragmentAddNewEventBinding.scrollView.getChildAt(fragmentAddNewEventBinding.scrollView.getChildCount() - 1)
                    val bottom: Int =
                        lastChild.bottom + fragmentAddNewEventBinding.scrollView.getPaddingBottom()
                    val sy: Int = fragmentAddNewEventBinding.scrollView.getScrollY()
                    val sh: Int = fragmentAddNewEventBinding.scrollView.getHeight()
                    val delta = bottom - (sy + sh)
                    fragmentAddNewEventBinding.scrollView.smoothScrollBy(0, delta)
                }, 200)
            }

        }*/
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

        addNewEventViewModel.eventMemberLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = result.data.payload
                    careteams.addAll(payload.data)
                    assigneeAdapter = AssigneAdapter(
                        this,
                        requireContext(),
                        careteams
                    )
                    fragmentEditCarePointBinding.assigneRV.adapter = assigneeAdapter


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
            R.id.assigneET -> {
                if (fragmentEditCarePointBinding.assigneRV.visibility == View.VISIBLE) {
                    fragmentEditCarePointBinding.assigneRV.visibility = View.GONE
                    rotate(0f)
                } else {
                    fragmentEditCarePointBinding.assigneRV.visibility = View.VISIBLE
                    rotate(180f)
                }
            }
            R.id.btnAdd -> {
                assignTo.clear()
                for (i in careteams) {
                    if (i.isSelected) {
                        assignTo.add(i.user_id_details.uid!!)
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
                fragmentEditCarePointBinding.tvDate.text =
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

                fragmentEditCarePointBinding.tvTime.text = ""
                isAmPm = null
                setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)
            }, mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = c.timeInMillis
        datePickerDialog.show()
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentEditCarePointBinding.etEventName.text.toString().trim().isEmpty() -> {
                    fragmentEditCarePointBinding.etEventName.error =
                        getString(R.string.please_enter_event_name)
                    fragmentEditCarePointBinding.etEventName.requestFocus()
                }
                assignTo.size <= 0 -> {
                    showInfo(
                        requireContext(),
                        getString(R.string.please_select_whome_to_assign_event)
                    )
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
                /* fragmentAddNewEventBinding.etNote.text.toString().trim().isEmpty() -> {
                     showInfo(
                         requireContext(),
                         getString(R.string.please_enter_notes_for_care_point)
                     )
                     fragmentAddNewEventBinding.etNote.requestFocus()
                 }*/
                else -> {
                    return true
                }
            }
            return false
        }

    private fun createEvent() {
        var selectedDate = fragmentEditCarePointBinding.tvDate.text.toString().trim()
        var dateFormat = SimpleDateFormat("MM-dd-yyyy hh:mm a")
        val formattedDate: Date = dateFormat.parse(
            selectedDate.plus(
                " ${
                    fragmentEditCarePointBinding.tvTime.text.toString().trim()
                } $isAmPm"
            )
        )!!
        dateFormat = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = dateFormat.format(formattedDate)
        dateFormat = SimpleDateFormat("HH:mm")
        val selectedTime = dateFormat.format(formattedDate)

        val note = if (fragmentEditCarePointBinding.etNote.text.toString().isNullOrEmpty()) {
            null
        } else {
            fragmentEditCarePointBinding.etNote.text.toString().trim()
        }

        /* addNewEventViewModel.createEvent(
             addNewEventViewModel.getLovedOneUUId(),
             fragmentEditCarePointBinding.etEventName.text.toString().trim(),
             fragmentEditCarePointBinding.edtAddress.text.toString().trim(),
             selectedDate,
             selectedTime,
             note,
             assignTo
         )*/
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }

    override fun onSelected(position: Int) {
        careteams[position].isSelected = !careteams[position].isSelected
        fragmentEditCarePointBinding.assigneRV.postDelayed({
            assigneeAdapter!!.notifyDataSetChanged()
        }, 100)
        val assignee: ArrayList<String> = arrayListOf()
        assignee.clear()
        for (i in careteams) {
            if (i.isSelected) {
                assignee.add(
                    i.user_id_details.firstname!!.plus(" ")
                        .plus(i.user_id_details.lastname?.ifEmpty { null })
                )
            }
        }
        if (assignee.size > 0) {
            fragmentEditCarePointBinding.assigneET.setText(assignee.joinToString())
        } else {
            fragmentEditCarePointBinding.assigneET.setText("")
        }

    }

    private fun timePicker() {
        if (fragmentEditCarePointBinding.tvDate.text.toString().trim().isEmpty()) {
            showError(requireContext(), getString(R.string.please_select_new_care_point_date_firts))
            fragmentEditCarePointBinding.tvDate.requestFocus()
        } else {
            val mCurrentTime = Calendar.getInstance()
            if (fragmentEditCarePointBinding.tvTime.text.isNotEmpty()) {
                val dateTime = fragmentEditCarePointBinding.tvDate.text.toString().trim().plus(" ")
                    .plus(fragmentEditCarePointBinding.tvTime.text.toString().plus(" $isAmPm"))
                mCurrentTime.time = SimpleDateFormat("dd-MM-yyyy hh:mm a").parse(dateTime)!!
            }
            val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
            val minute = mCurrentTime.get(Calendar.MINUTE)

            val mTimePicker = TimePickerDialog(
                context, R.style.datepicker,
                { _, hourOfDay, selectedMinute ->
                    //check event time for future events  only
                    val selectedDateTime =
                        fragmentEditCarePointBinding.tvDate.text.toString().trim().plus(" ")
                            .plus(String.format("%02d:%02d", hourOfDay, selectedMinute))
                    val currentDateTime =
                        SimpleDateFormat("MM-dd-yyyy HH:mm").format(Calendar.getInstance().time)

                    val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm")
                    if (dateFormat.parse(selectedDateTime)!!
                            .after(dateFormat.parse(currentDateTime))
                    ) {
                        if (hourOfDay < 12) {
                            isAmPm = "am"
                            setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                            fragmentEditCarePointBinding.tvTime.text =
                                String.format("%02d:%02d", hourOfDay, selectedMinute)
                        } else {
                            isAmPm = "pm"
                            setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                            fragmentEditCarePointBinding.tvTime.text =
                                String.format("%02d:%02d", hourOfDay - 12, selectedMinute)
                        }
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

}

