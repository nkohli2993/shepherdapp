package com.app.shepherd.ui.component.addNewEvent

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.care_team.CareTeam
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.databinding.FragmentAddNewEventBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.app.shepherd.utils.*
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showInfo
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.view_model.AddNewEventViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_new_event.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class AddNewEventFragment : BaseFragment<FragmentAddNewEventBinding>(),
    View.OnClickListener, AssignToEventAdapter.selectedTeamMember {

    private val addNewEventViewModel: AddNewEventViewModel by viewModels()

    private lateinit var fragmentAddNewEventBinding: FragmentAddNewEventBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var assignTo = ArrayList<String>()
    private var careteams = ArrayList<CareTeam>()
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


        initDatePicker()
       // initTimePicker()
        getAssignedToMembers()
        setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
        fragmentAddNewEventBinding.eventMemberSpinner.setOnTouchListener { v, event ->
            rotate(0f)
            if (event.action == MotionEvent.ACTION_DOWN) {
                rotate(180f)
            }
            false
        }

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
        addNewEventViewModel.createEventLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(requireContext(), "New carePoint added successfully...")
                    backPress()
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }

                }
            }

        }
        addNewEventViewModel.eventMemberLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = it.data.payload
                    careteams.add(CareTeam(id = -1))
                    careteams.addAll(payload.careTeams)
                    fragmentAddNewEventBinding.eventMemberSpinner.adapter =
                        AssignToEventAdapter(
                            this,
                            requireContext(),
                            addNewEventViewModel,
                            careteams
                        )

                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)
                    careteams.add(CareTeam())
                    fragmentAddNewEventBinding.eventMemberSpinner.adapter =
                        AssignToEventAdapter(
                            this,
                            requireContext(),
                            addNewEventViewModel,
                            careteams
                        )
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }

                }
            }
        }

    }

    private fun observeCreateEvent() {
        addNewEventViewModel.createEventLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()

                    Log.e("TAG", "observeEventMembers: " + it.data.payload)
                }

                is DataResult.Failure -> {
                    //handleAPIFailure(it.message, it.errorCode)

                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }

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


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnAdd -> {
                if (isValid) {
                    createEvent()
                }
            }
            R.id.clTimeWrapper ->{
                timePicker()
            }
        }
    }

    private val isValid: Boolean
        get() {
            when {
                fragmentAddNewEventBinding.etEventName.text.toString().trim().isEmpty() -> {
                    fragmentAddNewEventBinding.etEventName.error =
                        getString(R.string.please_enter_event_name)
                    fragmentAddNewEventBinding.etEventName.requestFocus()
                }
                fragmentAddNewEventBinding.edtAddress.text.toString().trim().isEmpty() -> {
                    fragmentAddNewEventBinding.edtAddress.error = getString(R.string.enter_address)
                    fragmentAddNewEventBinding.edtAddress.requestFocus()
                }
                fragmentAddNewEventBinding.tvDate.text.toString().trim() == "DD/MM/YY" -> {
                    showInfo(requireContext(), getString(R.string.please_enter_date_of_birth))
                }
                else -> {
                    return true
                }
            }
            return false
        }

    @SuppressLint("SimpleDateFormat")
    private fun createEvent() {
        assignTo.clear()
        for (i in careteams) {
            if (i.isSelected) {
                assignTo.add(i.userId!!)
            }

        }
        var selectedDate = fragmentAddNewEventBinding.tvDate.text.toString().trim()
        var dateFormat = SimpleDateFormat("dd MMM yyyy")
        val formatedDate: Date = dateFormat.parse(selectedDate)!!
        dateFormat = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = dateFormat.format(formatedDate)
        addNewEventViewModel.createEvent(
            addNewEventViewModel.getLovedOneUUId(),
            fragmentAddNewEventBinding.etEventName.text.toString().trim(),
            fragmentAddNewEventBinding.edtAddress.text.toString().trim(),
            selectedDate,
            fragmentAddNewEventBinding.tvTime.text.toString().trim(),
            fragmentAddNewEventBinding.etNote.text.toString().trim(),
            assignTo
        )
    }


    private fun initDatePicker() {
        tvDate.datePicker(
            childFragmentManager,
            AddNewEventFragment::class.java.simpleName
        )
    }

    private fun initTimePicker() {
       tvTime.timePicker(
            requireContext()
        )
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }

    override fun onSelected(position: Int) {
        // on team member selected
        careteams[position].isSelected = !careteams[position].isSelected
    }

    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context,
            { view, hourOfDay, selectedMinute ->
                fragmentAddNewEventBinding.tvTime.text =
                    String.format("%02d:%02d", hourOfDay, selectedMinute)

                if (hourOfDay < 12) {
                    setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                } else {
                    setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                }
            }, hour,
            minute, true
        )
        mTimePicker.show()
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

}

