package com.shepherd.app.ui.component.addNewEvent

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.shepherd.app.R
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.databinding.FragmentAddNewEventBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewEvent.adapter.AssignToEventAdapter
import com.shepherd.app.ui.component.addNewEvent.adapter.AssigneAdapter
import com.shepherd.app.utils.*
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.AddNewEventViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
@SuppressLint("SimpleDateFormat")
class AddNewEventFragment : BaseFragment<FragmentAddNewEventBinding>(),
    View.OnClickListener, AssigneAdapter.selectedTeamMember,
    DatePickerDialog.OnDateSetListener, AssignToEventAdapter.selectedTeamMember {

    private var assigneAdapter: AssigneAdapter? = null
    private val addNewEventViewModel: AddNewEventViewModel by viewModels()

    private lateinit var fragmentAddNewEventBinding: FragmentAddNewEventBinding
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var status: Int = 1
    private var assignTo = ArrayList<String>()
    private var careteams = ArrayList<CareTeamModel>()
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


        getAssignedToMembers()
        setColorTimePicked(R.color.colorBlackTrans50, R.color.colorBlackTrans50)
        fragmentAddNewEventBinding.etNote.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
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
                    careteams.addAll(payload.data)
                  /*  assigneAdapter =  AssignToEventAdapter(
                        this,
                        requireContext(),
                        addNewEventViewModel,
                        careteams
                    )*/
                    assigneAdapter = AssigneAdapter( this,
                        requireContext(),
                        addNewEventViewModel,
                        careteams)
                    fragmentAddNewEventBinding.assigneRV.adapter = assigneAdapter


                }

                is DataResult.Failure -> {
                    careteams.add(CareTeamModel())
                   /* fragmentAddNewEventBinding.eventMemberSpinner.adapter =
                        AssignToEventAdapter(
                            this,
                            requireContext(),
                            addNewEventViewModel,
                            careteams
                        )*/
                    assigneAdapter = AssigneAdapter( this,
                        requireContext(),
                        addNewEventViewModel,
                        careteams)
                    fragmentAddNewEventBinding.assigneRV.adapter = assigneAdapter
                    hideLoading()
                    it.message?.let { showError(requireContext(), it) }

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
                }

                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it) }

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

    @SuppressLint("SetTextI18n")
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.assigneET ->{
                if(fragmentAddNewEventBinding.assigneRV.visibility == View.VISIBLE){
                    fragmentAddNewEventBinding.assigneRV.visibility= View.GONE
                    rotate(0f)
                }
                else{
                    fragmentAddNewEventBinding.assigneRV.visibility= View.VISIBLE
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
            R.id.clTimeWrapper -> {
                timePicker()
            }
            R.id.tvDate -> {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val datePickerDialog = DatePickerDialog(
                    requireActivity(),R.style.datepicker,
                    { _, year, monthOfYear, dayOfMonth ->
                        fragmentAddNewEventBinding.tvDate.text = dayOfMonth.toString() + "-" +if(monthOfYear+1 <10){"0${(monthOfYear + 1)}"} else{(monthOfYear + 1)}  + "-" + year
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.datePicker.minDate = c.timeInMillis
                datePickerDialog.show()
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
                fragmentAddNewEventBinding.etNote.text.toString().trim().isEmpty() -> {
                    showInfo(
                        requireContext(),
                        getString(R.string.please_enter_notes_for_care_point)
                    )
                    fragmentAddNewEventBinding.etNote.requestFocus()
                }
                else -> {
                    return true
                }
            }
            return false
        }

    private fun createEvent() {
        var selectedDate = fragmentAddNewEventBinding.tvDate.text.toString().trim()
        var dateFormat = SimpleDateFormat("dd-MM-yyyy")
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

    override fun getLayoutRes(): Int {
        return R.layout.fragment_add_new_event
    }

    override fun onSelected(position: Int) {
        careteams[position].isSelected = !careteams[position].isSelected
        Handler(Looper.myLooper()!!).post { assigneAdapter!!.notifyDataSetChanged() }


        val assignee :ArrayList<String> = arrayListOf()
        assignee.clear()
        for(i in careteams){
            if(i.isSelected){
                assignee.add(i.user_id_details.firstname!!.plus(" ").plus(i.user_id_details.lastname))
            }
        }
        if(assignee.size>0){
            fragmentAddNewEventBinding.assigneET.setText(assignee.joinToString())
        }else{
            fragmentAddNewEventBinding.assigneET.setText("")
        }

    }

    private fun timePicker() {
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        val mTimePicker = TimePickerDialog(
            context,R.style.datepicker,
            { _, hourOfDay, selectedMinute ->
                //check event time for future events  only
                if(fragmentAddNewEventBinding.tvDate.text.toString().trim().isEmpty()){
                    showError(requireContext(),"Please select new CarePoint date first.")
                    fragmentAddNewEventBinding.tvDate.requestFocus()
                }
                else{
                    var selectedDateTime = fragmentAddNewEventBinding.tvDate.text.toString().trim().plus(" ").plus(String.format("%02d:%02d", hourOfDay, selectedMinute))
                    val currentDateTime = SimpleDateFormat("dd-MM-yyyy HH:mm").format(Calendar.getInstance().time)

                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    if(dateFormat.parse(selectedDateTime)!!.after(dateFormat.parse(currentDateTime))){
                        fragmentAddNewEventBinding.tvTime.text =
                            String.format("%02d:%02d", hourOfDay, selectedMinute)

                        if (hourOfDay < 12) {
                            setColorTimePicked(R.color._192032, R.color.colorBlackTrans50)
                        } else {
                            setColorTimePicked(R.color.colorBlackTrans50, R.color._192032)
                        }
                    }
                    else{
                        showError(requireContext(),"Please select future time for new CarePoint.")
                    }
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


    override fun onDateSet(p0: android.widget.DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val mCalendar = Calendar.getInstance()
        mCalendar[Calendar.YEAR] = year
        mCalendar[Calendar.MONTH] = month
        mCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
        val selectedDate: String =
            SimpleDateFormat("dd/MMM/yyyy").format(mCalendar.time)
        fragmentAddNewEventBinding.tvDate.text = selectedDate
    }

}

