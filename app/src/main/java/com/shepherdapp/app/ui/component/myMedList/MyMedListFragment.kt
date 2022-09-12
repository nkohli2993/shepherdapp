package com.shepherdapp.app.ui.component.myMedList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
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
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.med_list.get_medication_record.MedicationRecordData
import com.shepherdapp.app.data.dto.med_list.loved_one_med_list.UserMedicationData
import com.shepherdapp.app.data.dto.med_list.loved_one_med_list.UserMedicationRemiderData
import com.shepherdapp.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherdapp.app.databinding.FragmentMyMedlistBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.addNewMedication.AddNewMedicationFragmentDirections
import com.shepherdapp.app.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.shepherdapp.app.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.shepherdapp.app.utils.MedListAction
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observeEvent
import com.shepherdapp.app.view_model.MyMedListViewModel
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
    private var medListReminderList: ArrayList<UserMedicationRemiderData> = arrayListOf()
    private var payload: ArrayList<UserMedicationData> = arrayListOf()
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private var dayId = ""
    private var selectedDate = ""
    private var TAG = "MyMedListFragment"
    private var medicationRecordPayload: ArrayList<MedicationRecordData> = arrayListOf()
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
/*
                    medListViewModel.getMedicationRecords(
                        medListViewModel.getLovedOneUUId() ?: "",
                        1,
                        1000,
                        selectedDate
                    )
*/
                    medListViewModel.getLovedOneMedLists(selectedDate)
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
            pastDaysCount = 30
            init()
            includeCurrentDate = true
            initialPositionIndex = 30
            scrollToPosition(30)
            select(30)
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
//                    payload = it.data.payload!!.userMedicationAll

                    for (i in it.data.payload!!.userMedicationAll) {
                        val medicationDetailData = i
                        i.selectedDate = selectedDate
                        payload.add(medicationDetailData)
                    }

                    if (payload.size <= 0) {
                        myMedlistBinding.recyclerViewMyMedications.visibility = View.GONE
                        myMedlistBinding.txtMedication.visibility = View.VISIBLE
                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.GONE
                        myMedlistBinding.txtNoMedicationReminder.visibility = View.VISIBLE
                    }
                    if (payload.isEmpty()) return@observeEvent

                    for (i in it.data.payload!!.userMedicationRepeat) {
                        if (i.days!!.contains(dayId)) {
                            if((i.frequency?:"0").toInt()<5){
                                val medListReminder = i
                                i.selectedDate = selectedDate
                                medListReminderList.add(medListReminder)
                            }
                        }
                    }
                    if (medListReminderList.size <= 0) {
                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.GONE
                        myMedlistBinding.txtNoMedicationReminder.visibility = View.VISIBLE
                    } else {

                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.VISIBLE
                        myMedlistBinding.txtNoMedicationReminder.visibility = View.GONE
                        selectedDayMedicineAdapter?.addData(medListReminderList)
                    }
                    if (payload.size <= 0) {
                        myMedlistBinding.recyclerViewMyMedications.visibility = View.GONE
                        myMedlistBinding.txtMedication.visibility = View.VISIBLE
                    } else {

                        myMedlistBinding.recyclerViewMyMedications.visibility = View.VISIBLE
                        myMedlistBinding.txtMedication.visibility = View.GONE
                        myMedicationsAdapter?.addData(payload)
                    }


                }
            }
        }
        // Observe get loved one med lists response
        medListViewModel.getMedicationRecordResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    payload.clear()
                    medListReminderList.clear()
                    medListViewModel.getLovedOneMedLists(selectedDate)
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    // records for medication
                    medicationRecordPayload = it.data.payload.data
                    payload.clear()
                    medListReminderList.clear()
                    medListViewModel.getLovedOneMedLists(selectedDate)
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
                    medListViewModel.getLovedOneMedLists(selectedDate)

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
                    showSuccess(
                        requireContext(),
                        getString(R.string.medication_record_added_successfully)
                    )
                }
            }
        }
    }

    private fun medListDataBasedOnAllDates() {
/*
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
                    val data = it.data.payload!!.userMedicationAll
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
                    val medListList: ArrayList<MedListReminder> = arrayListOf()
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
                                payload.dosage,
                                selectedDate
                            )
                            medListList.add(medListRem)
                        }
                    }
                    val matchedList: ArrayList<MedListReminder> = arrayListOf()
                    for (i in medListList) {
                        var found = false
                        for (j in medicationRecordPayload) {
                            if (i.selectedDate == j.date && i.time!!.time!!.plus(" ")
                                    .plus(i.time!!.hour) == j.time!!
                            ) {
                                found = true
                            }
                        }
                        if (found) {
                            val data = i
                            data.isSelected = true
                            matchedList.add(data)
                        }
                    }
                    medListList.removeAll(matchedList)
                    medListReminderList.addAll(medListList)
                    medListReminderList.addAll(matchedList)

                    // do sort the list based on time
                    medListReminderList.sortWith { o1, o2 ->

                        o1.selectedDate.plus(
                            " ${
                                o1.time!!.time!!.plus(" ").plus(o1.time!!.hour)
                            }"
                        )
                            .compareTo(
                                o2.selectedDate.plus(
                                    " ${
                                        o2.time!!.time!!.plus(" ").plus(o2.time!!.hour)
                                    }"
                                )
                            )
                    }
                    selectedDayMedicineAdapter?.addData(medListReminderList)
                    myMedicationsAdapter?.addData(payload)
                }
            }
        }
*/
    }

    private fun recordMedication(singleEvent: SingleEvent<UserMedicationRemiderData>) {
        singleEvent.getContentIfNotHandled()?.let {
            if (it.isRecordAdded!!) {
                val date = it.selectedDate
                val time = it.time?.time + " " + it.time?.hour
                val medicationRecordRequest =
                    it.id?.let { it1 -> MedicationRecordRequestModel(it1, date!!, time) }
                medicationRecordRequest?.let { it1 -> medListViewModel.addUserMedicationRecord(it1) }
            }
        }
    }

    private fun selectedMedication(navigateEvent: SingleEvent<UserMedicationData>) {
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

    private fun navigateToMedDetail(navigateEvent: SingleEvent<UserMedicationRemiderData>) {
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
                            medicationId = it.id.toString(),
                            medicationUpdateDate = it.selectedDate
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