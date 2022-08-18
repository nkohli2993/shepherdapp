package com.shepherd.app.ui.component.myMedList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherd.app.databinding.FragmentMyMedlistBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewMedication.AddNewMedicationFragmentDirections
import com.shepherd.app.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.shepherd.app.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.shepherd.app.utils.MedListAction
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.utils.observeEvent
import com.shepherd.app.view_model.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MyMedListFragment : BaseFragment<FragmentMyMedlistBinding>() {

    private val medListViewModel: MyMedListViewModel by viewModels()
    private var myMedicationsAdapter: MyMedicationsAdapter? = null
    private var selectedDayMedicineAdapter: SelectedDayMedicineAdapter? = null
    private lateinit var myMedlistBinding: FragmentMyMedlistBinding
    private var deletePosition: Int = -1
    private var medListReminderList: ArrayList<MedListReminder> = arrayListOf()
    private var payload: ArrayList<Payload> = arrayListOf()
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private var dayId = ""
    private var selectedDate = ""
    private var TAG = "MyMedListFragment"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        myMedlistBinding =
            FragmentMyMedlistBinding.inflate(inflater, container, false)
        return myMedlistBinding.root
    }

    override fun initViewBinding() {
        // Get Loved One's Medication Listing
        setMyMedicationsAdapter()
        setSelectedDayMedicineAdapter()
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]
        setCalender()
    }

    private fun setCalender() {
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                val cal = Calendar.getInstance()
                cal.time = date
                return if (isSelected)
                    R.layout.selected_calendar_item
                else
                    R.layout.calendar_item
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                holder.itemView.tv_date_calendar_item.text = DateUtils.getDayNumber(date)
                holder.itemView.tv_day_calendar_item.text = DateUtils.getDay3LettersName(date)

            }
        }
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            @SuppressLint("SimpleDateFormat")
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
                // check day id based on date id
                val day = SimpleDateFormat("EEE").format(date)
                selectedDate = SimpleDateFormat("yyyy-MM-dd").format(date)
                dayId = when (day) {
                    "Mon" -> "1"
                    "Tue" -> "2"
                    "Wed" -> "3"
                    "Thu" -> "4"
                    "Fri" -> "5"
                    "Sat" -> "6"
                    else -> "7"
                }
                if (isSelected) {
                    payload.clear()
                    medListReminderList.clear()
                    medListViewModel.getLovedOneMedLists()
                }
            }
        }
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> true
                    Calendar.SUNDAY -> true
                    else -> true
                }
            }
        }
        val singleRowCalendar = myMedlistBinding.srCalender.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 30
            includeCurrentDate = true
            init()
            select(0)
        }

    }

    @SuppressLint("NotifyDataSetChanged", "SimpleDateFormat")
    override fun observeViewModel() {
        observeEvent(medListViewModel.openMedDetailItems, ::navigateToMedDetail)
        observeEvent(medListViewModel.medDetailItems, ::selectedMedication)
        observeEvent(medListViewModel.selectedMedicationLiveData, ::recordMedication)
        // Observe get loved one med lists response
        medListViewModel.getLovedOneMedListsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.GONE
                    myMedlistBinding.recyclerViewMyMedications.visibility = View.GONE
                    myMedlistBinding.txtNoMedicationReminder.visibility = View.VISIBLE
                    myMedlistBinding.txtMedication.visibility = View.VISIBLE
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    payload.clear()
                    medListReminderList.clear()
                    myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.VISIBLE
                    myMedlistBinding.recyclerViewMyMedications.visibility = View.VISIBLE
                    myMedlistBinding.txtNoMedicationReminder.visibility = View.GONE
                    myMedlistBinding.txtMedication.visibility = View.GONE
                    // check day for payload data
                    val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(selectedDate)
                    val data = it.data.payload
                    for (i in data) {
                        if (i.days!!.contains(dayId)) {
                            if (i.endDate != null) {
                                if (SimpleDateFormat("yyyy-MM-dd").parse(i.endDate!!)!! == currentDate || SimpleDateFormat(
                                        "yyyy-MM-dd"
                                    ).parse(i.endDate!!)!!.after(currentDate)
                                ) {
                                    payload.add(i)
                                }
                            } else {
                                payload.add(i)
                            }
                        }
                    }
                    if (payload.size <= 0) {
                        myMedlistBinding.recyclerViewMyMedications.visibility = View.GONE
                        myMedlistBinding.txtMedication.visibility = View.VISIBLE
                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.GONE
                        myMedlistBinding.txtNoMedicationReminder.visibility = View.VISIBLE
                    }
                    if (payload.isEmpty()) return@observeEvent
                    for (i in payload.indices) {
                        val payload = payload[i]
                        payload.time.forEach {
                            val medListRem = MedListReminder(
                                payload.id,
                                payload.assignedBy,
                                payload.assignedTo,
                                payload.loveUserId,
                                payload.dosageId,
                                payload.medlistId,
                                payload.frequency,
                                payload.days,
                                it,
                                payload.note,
                                payload.createdAt,
                                payload.updatedAt,
                                payload.deletedAt,
                                payload.medlist,
                                payload.endDate,
                                false,
                                payload.dosage
                            )
                            medListReminderList.add(medListRem)
                        }
                    }
                    selectedDayMedicineAdapter?.addData(medListReminderList)
                    myMedicationsAdapter?.addData(payload)
                }
            }
        }

        // observe when medlist deleted from list
        medListViewModel.deletedScheduledMedicationResponseLiveData.observeEvent(this) {
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
                    showInfo(
                        requireContext(),
                        getString(R.string.schedule_medication_deleted_successfully)
                    )
                    medListViewModel.getLovedOneMedLists()

                }
            }
        }

        // Observe medication record response live data
        medListViewModel.medicationRecordResponseLiveData.observeEvent(this) {
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
                    showSuccess(requireContext(), "Medication Record Added Successfully...")
                }
            }
        }
    }

    private fun recordMedication(singleEvent: SingleEvent<MedListReminder>) {
        singleEvent.getContentIfNotHandled()?.let {
            if (it.isSelected) {
                Log.d(TAG, "selectedMedication: $it")
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                val date = sdf.format(Date())
                val time = it.time?.time + " " + it.time?.hour
                val medicationRecordRequest =
                    it.id?.let { it1 -> MedicationRecordRequestModel(it1, date, time) }
                medicationRecordRequest?.let { it1 -> medListViewModel.addUserMedicationRecord(it1) }
            }
        }
    }

    private fun selectedMedication(navigateEvent: SingleEvent<Payload>) {
        navigateEvent.getContentIfNotHandled()?.let {
            when (it.actionType ?: MedListAction.View.value) {
                MedListAction.View.value -> {
                    val bundle = Bundle()
                    it.id?.let { it1 -> bundle.putInt("id", it1) }
                    findNavController().navigate(R.id.action_my_medlist_to_med_detail, bundle)
                }
                MedListAction.EDIT.value -> {
                    //open edit view
                    findNavController().navigate(
                        AddNewMedicationFragmentDirections.actionAddNewMedicationToAddMedication(
                            medicationId = it.id.toString()
                        )
                    )
                }
                MedListAction.Delete.value -> {
                    //delete medlist schedules
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle(getString(R.string.delete_medication_schedules))
                        setMessage(getString(R.string.sure_you_want_to_delte_medication))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            deletePosition = it.id!!
                            medListViewModel.deletedSceduledMedication(it.id!!)
                        }
                        setNegativeButton(getString(R.string.cancel)) { _, _ ->

                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                }
            }

        }
    }

    private fun navigateToMedDetail(navigateEvent: SingleEvent<MedListReminder>) {
        navigateEvent.getContentIfNotHandled()?.let {
            when (it.medlist?.actionType ?: MedListAction.View.value) {
                MedListAction.View.value -> {
                    val bundle = Bundle()
                    it.id?.let { it1 -> bundle.putInt("id", it1) }
                    findNavController().navigate(R.id.action_my_medlist_to_med_detail, bundle)
                }
                MedListAction.EDIT.value -> {
                    //open edit view
                    findNavController().navigate(
                        AddNewMedicationFragmentDirections.actionAddNewMedicationToAddMedication(
                            medicationId = it.id.toString()
                        )
                    )
                }
                MedListAction.Delete.value -> {
                    //delete medlist schedules
                    //show delete dialog
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Delete Medication Schedule")
                        setMessage("Sure you want to delete this medication schedule")
                        setPositiveButton("Yes") { _, _ ->
                            deletePosition = it.id!!
                            medListViewModel.deletedSceduledMedication(it.id!!)
                        }
                        setNegativeButton("Cancel") { _, _ ->

                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
                }
            }

        }
    }


    private fun setMyMedicationsAdapter() {
        myMedicationsAdapter = MyMedicationsAdapter(medListViewModel)
        myMedlistBinding.recyclerViewMyMedications.adapter = myMedicationsAdapter

    }

    private fun setSelectedDayMedicineAdapter() {
        selectedDayMedicineAdapter = SelectedDayMedicineAdapter(medListViewModel)
        myMedlistBinding.recyclerViewSelectedDayMedicine.adapter = selectedDayMedicineAdapter

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_medlist
    }


}

