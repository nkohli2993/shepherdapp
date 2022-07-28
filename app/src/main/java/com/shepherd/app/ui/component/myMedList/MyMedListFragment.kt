package com.shepherd.app.ui.component.myMedList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import com.shepherd.app.R
import androidx.navigation.fragment.findNavController
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionResponseModel
import com.shepherd.app.databinding.FragmentMyMedlistBinding
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.myMedList.adapter.MyMedicationsAdapter
import com.shepherd.app.ui.component.myMedList.adapter.SelectedDayMedicineAdapter
import com.shepherd.app.utils.*
import com.shepherd.app.view_model.MedListViewModel
import com.google.android.material.snackbar.Snackbar
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*


/**
 * Created by Sumit Kumar on 26-04-22
 */
@AndroidEntryPoint
class MyMedListFragment : BaseFragment<FragmentMyMedlistBinding>() {

    private val medListViewModel: MedListViewModel by viewModels()

    private lateinit var myMedlistBinding: FragmentMyMedlistBinding

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

    override fun observeViewModel() {
//        observe(medListViewModel._medicalConditionResponseLiveData, ::handleLoginResult)
        observeSnackBarMessages(medListViewModel.showSnackBar)
        observeToast(medListViewModel.showToast)
//        observeEvent(medListViewModel.openMedDetailItems, ::navigateToMedDetail)
    }

    private fun navigateToMedDetail(navigateEvent: SingleEvent<String>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(R.id.action_my_medlist_to_med_detail)
        }
    }

    private fun handleLoginResult(status: Resource<MedicalConditionResponseModel>) {
        when (status) {
            is Resource.Loading -> {}
            is Resource.Success -> status.data?.let {

            }
            is Resource.DataError -> {
                status.errorCode?.let { medListViewModel.showToastMessage(it) }
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
        val myMedicationsAdapter = MyMedicationsAdapter(medListViewModel)
        myMedlistBinding.recyclerViewMyMedications.adapter = myMedicationsAdapter

    }

    private fun setSelectedDayMedicineAdapter() {
        val selectedDayMedicineAdapter = SelectedDayMedicineAdapter(medListViewModel)
        myMedlistBinding.recyclerViewSelectedDayMedicine.adapter = selectedDayMedicineAdapter

    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_medlist
    }


}

