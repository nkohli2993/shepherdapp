package com.shepherd.app.ui.component.myMedList

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.databinding.FragmentMyMedlistBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.addNewMedication.AddNewMedicationFragmentDirections
import com.shepherd.app.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.shepherd.app.utils.*
import com.shepherd.app.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.utils.setupSnackbar
import com.shepherd.app.utils.showToast
import com.shepherd.app.view_model.MyMedListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.calendar_item.view.*
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

    private var deletePostion: Int = -1
    private var pageNumber = 1
    private val limit = 10
    var currentPage: Int = 0
    var totalPage: Int = 0
    var total: Int = 0
    var pageCount: Int = 0
    var medListReminderList: ArrayList<MedListReminder> = arrayListOf()


    //    var medlists: ArrayList<Medlists> = arrayListOf()
    var payload: ArrayList<Payload> = arrayListOf()
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
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
        medListViewModel.getLovedOneMedLists()
//        medListViewModel.getAllMedLists(pageNumber, limit)
//        setRemindersAdapter()
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
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
            }


        }
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                val cal = Calendar.getInstance()
                cal.time = date
                return when (cal[Calendar.DAY_OF_WEEK]) {
                    Calendar.SATURDAY -> false
                    Calendar.SUNDAY -> false
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
//            setDates(getFutureDatesOfCurrentMonth())
            init()
            select(0)
        }
//        singleRowCalendar.set
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calendar[Calendar.MONTH]
        return getDates(mutableListOf())
    }

    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calendar.time)
        while (currentMonth == calendar[Calendar.MONTH]) {
            calendar.add(Calendar.DATE, +1)
            if (calendar[Calendar.MONTH] == currentMonth)
                list.add(calendar.time)
        }
        calendar.add(Calendar.DATE, -1)
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun observeViewModel() {
        observeEvent(medListViewModel.openMedDetailItems, ::navigateToMedDetail)

        // Observe Get All Med List Live Data
        /*   medListViewModel.getMedListResponseLiveData.observeEvent(this) {
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
                           medLists = payload?.medlists!!
                           total = payload.total!!
                           currentPage = payload.currentPage!!
                           totalPage = payload.totalPages!!

                       }

                       if (medLists.isNullOrEmpty()) return@observeEvent
                       myMedicationsAdapter?.addData(medLists)

                   }
               }
           }*/

        // Observe get loved one med lists response
        medListViewModel.getLovedOneMedListsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
//                    showError(requireContext(), it.message.toString())
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
//                    medlists = it.data.payload?.medlists!!
                    payload = it.data.payload
                    if (payload.isNullOrEmpty()) return@observeEvent
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
                                payload.medlist
                            )
                            medListReminderList.add(medListRem)
                        }
                    }
                    myMedicationsAdapter?.addData(payload)
                    selectedDayMedicineAdapter?.addData(medListReminderList)
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
                    showInfo(requireContext(), getString(R.string.schedule_medication_deleted_successfully))
                    //remove from list
                    payload.removeAt(deletePostion)
                    myMedicationsAdapter!!.addData(payload)
                    myMedicationsAdapter!!.notifyDataSetChanged()
                    if (payload.isNullOrEmpty()) {
                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.GONE
                        myMedlistBinding.txtNoScehduled.visibility =
                            View.VISIBLE
                    } else {
                        myMedlistBinding.recyclerViewSelectedDayMedicine.visibility = View.VISIBLE
                        myMedlistBinding.txtNoScehduled.visibility = View.GONE
                    }

                }
            }
        }

    }

    private fun navigateToMedDetail(navigateEvent: SingleEvent<Payload>) {
        navigateEvent.getContentIfNotHandled()?.let {
            when(it.actionType?:MedListAction.View.value){
                MedListAction.View.value ->{
                    findNavController().navigate(R.id.action_my_medlist_to_med_detail)
                }
                MedListAction.EDIT.value ->{
                    //open edit view
                    findNavController().navigate(
                        AddNewMedicationFragmentDirections.actionAddNewMedicationToAddMedication(
                          medicationScheduled = it
                        ))
                }
                MedListAction.Delete.value ->{
                    //delete medlist schedules
                    //show delete dialog
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Delete Medication Schedule")
                        setMessage("Sure you want to delete this medication schedule")
                        setPositiveButton("Yes") { _, _ ->
                            deletePostion = it.deletePosition!!
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


    private fun observeSnackBarMessages(event: LiveData<SingleEvent<Any>>) {
        myMedlistBinding.root.setupSnackbar(this, event, Snackbar.LENGTH_LONG)
    }

    private fun observeToast(event: LiveData<SingleEvent<Any>>) {
        myMedlistBinding.root.showToast(this, event, Snackbar.LENGTH_LONG)
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

