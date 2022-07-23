package com.app.shepherd.ui.component.carePoints


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.shepherd.R
import com.app.shepherd.data.dto.added_events.ResultEventModel
import com.app.shepherd.databinding.FragmentCarePointsBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseFragment
import com.app.shepherd.ui.component.carePoints.adapter.CarePointsDayAdapter
import com.app.shepherd.utils.CalendarState
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observe
import com.app.shepherd.view_model.CreatedCarePointsViewModel
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Sumit Kumar on 26-04-22
 */

@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class CarePointsFragment : BaseFragment<FragmentCarePointsBinding>(),
    View.OnClickListener, CarePointsDayAdapter.EventSelected {
    private lateinit var fragmentCarePointsBinding: FragmentCarePointsBinding
    private var carePoints: ArrayList<ResultEventModel>? = ArrayList()
    private var carePointsAdapter: CarePointsDayAdapter? = null
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var startDate: String = ""
    private var endDate: String = ""
    private var clickType = CalendarState.Today.value
    private var sdf: SimpleDateFormat? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentCarePointsBinding =
            FragmentCarePointsBinding.inflate(inflater, container, false)

        return fragmentCarePointsBinding.root
    }

    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        fragmentCarePointsBinding.listener = this
        sdf = SimpleDateFormat("yyyy-MM-dd")
        startDate = sdf!!.format(Calendar.getInstance().time)
        endDate = startDate
        getCarePointList(startDate,endDate)
//        fragmentCarePointsBinding.calendar.setForwardButtonImage(R.drawable.ic_right_arrow);
//        fragmentCarePointsBinding.calendar.setPreviousButtonImage(R.drawable.ic_left_arrow);

        fragmentCarePointsBinding.calendar.setDate(Calendar.getInstance())
        fragmentCarePointsBinding.calendar.setOnPreviousPageChangeListener(object :OnCalendarPageChangeListener{
            override fun onChange() {
                getDateSelectedOnTypeBased(fragmentCarePointsBinding.calendar.currentPageDate.time)
            }
        })
        fragmentCarePointsBinding.calendar.setOnForwardPageChangeListener(object :OnCalendarPageChangeListener{
            override fun onChange() {
                getDateSelectedOnTypeBased(fragmentCarePointsBinding.calendar.currentPageDate.time)
            }
        })
        val calendars: ArrayList<Calendar> = ArrayList()
        calendars.add(Calendar.getInstance())
        fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
        fragmentCarePointsBinding.calendar.setSelectionBackground(R.drawable.background_black_round)

      //  fragmentCarePointsBinding.calendar.set
        fragmentCarePointsBinding.calendar.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                when (clickType) {
                    CalendarState.Today.value -> {
                        startDate = sdf!!.format(clickedDayCalendar.time)
                        endDate = sdf!!.format(clickedDayCalendar.time)
                        getCarePointList(startDate,endDate)
                        val calendarsHighLights: ArrayList<Calendar> = ArrayList()
                        calendarsHighLights.add(clickedDayCalendar)
                        fragmentCarePointsBinding.calendar.setHighlightedDays(calendarsHighLights)
                        fragmentCarePointsBinding.calendar.selectedDates = calendarsHighLights
                        fragmentCarePointsBinding.calendar.setSelectionBackground(R.drawable.background_black_round)
                    }
                    CalendarState.Week.value -> {
                        startDate = sdf!!.format(clickedDayCalendar.time)
                        clickedDayCalendar.add(Calendar.DATE, 7)
                        endDate = sdf!!.format(clickedDayCalendar.time)
                        getCarePointList(startDate,endDate)
                        clickedDayCalendar.add(Calendar.DATE,-7)
                        val calendarsHighLights: ArrayList<Calendar> = ArrayList()
                        for (i in 0 until 7) {
                            val cal = Calendar.getInstance()
                            cal.time = clickedDayCalendar.time
                            cal.add(Calendar.DATE, i)
                            calendarsHighLights.add(cal)
                        }
                        fragmentCarePointsBinding.calendar.setHighlightedDays(calendarsHighLights)
                        fragmentCarePointsBinding.calendar.selectedDates = calendarsHighLights
                        fragmentCarePointsBinding.calendar.setSelectionBackground(R.drawable.background_black_round)
                    }
                    CalendarState.Month.value -> {
                        startDate = sdf!!.format(clickedDayCalendar.time)
                        clickedDayCalendar.add(Calendar.MONTH, 1)
                        endDate = sdf!!.format(clickedDayCalendar.time)
                        getCarePointList(startDate,endDate)
                        clickedDayCalendar.add(Calendar.MONTH,-1)
                        val calendarsHighLights: ArrayList<Calendar> = ArrayList()
                        for (i in 0 until 30) {
                            val cal = Calendar.getInstance()
                            cal.time = clickedDayCalendar.time
                            cal.add(Calendar.DATE,i)
                            calendarsHighLights.add(cal)
                        }
                        fragmentCarePointsBinding.calendar.setHighlightedDays(calendarsHighLights)
                        fragmentCarePointsBinding.calendar.selectedDates = calendarsHighLights
                        fragmentCarePointsBinding.calendar.setSelectionBackground(R.drawable.background_black_round)
                    }
                }


            }
        })
    }

    private fun getDateSelectedOnTypeBased(selectedDate: Date) {
        when (clickType) {
            CalendarState.Today.value -> {
                setSelectedDate()
                startDate = sdf!!.format(selectedDate)
                endDate = sdf!!.format(selectedDate)
                getCarePointList(startDate, endDate)
            }
            CalendarState.Week.value -> {
                setWeekBasedValues()
            }
            CalendarState.Month.value -> {
                setMonthBasedCalendarValue()
            }
        }
    }

    private fun setWeekBasedValues() {
        setSelectedDate()
        val calendar = fragmentCarePointsBinding.calendar.currentPageDate
        startDate = sdf!!.format(calendar.time)
        calendar.add(Calendar.DATE, 7)
        endDate = sdf!!.format(calendar.time)
        getCarePointList(startDate, endDate)
        val calendars: ArrayList<Calendar> = ArrayList()
        for (i in 0 until 7) {
            val cal = Calendar.getInstance()
            cal.time = fragmentCarePointsBinding.calendar.currentPageDate.time
            cal.add(Calendar.DATE, i)
            calendars.add(cal)
        }
        fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
    }

    private fun setMonthBasedCalendarValue() {
        setSelectedDate()
        val calendar = fragmentCarePointsBinding.calendar.currentPageDate
        startDate = sdf!!.format(calendar.time)
        calendar.add(Calendar.DATE, 30)
        endDate = sdf!!.format(calendar.time)
        getCarePointList(startDate, endDate)
        val calendars: ArrayList<Calendar> = ArrayList()
        for (i in 0 until 30) {
            val cal = Calendar.getInstance()
            cal.time = fragmentCarePointsBinding.calendar.currentPageDate.time
            cal.add(Calendar.DATE, i)
            calendars.add(cal)
        }
        fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
    }

    private fun setSelectedDate() {
        val calendars: ArrayList<Calendar> = ArrayList()
        calendars.add(fragmentCarePointsBinding.calendar.currentPageDate)
        fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
    }

    override fun observeViewModel() {
        observe(carePointsViewModel.openMemberDetails, ::openCarePointDetails)
        carePointsViewModel.carePointsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    fragmentCarePointsBinding.noCareFoundTV.visibility = View.GONE
                    carePoints = it.data.payload.results
                    Collections.sort(carePoints!!, object : Comparator<ResultEventModel?> {
                        var df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                        override fun compare(o1: ResultEventModel?, o2: ResultEventModel?): Int {
                            return try {
                                df.parse(o1!!.date!!)!!.compareTo(df.parse(o2!!.date!!))
                            } catch (e: Exception) {
                                throw IllegalArgumentException(e)
                            }
                        }
                    })

                    if (carePoints.isNullOrEmpty()) return@observeEvent
                    carePointsAdapter?.updateCarePoints(carePoints!!)
                    setCarePointsAdapter()
                }
                is DataResult.Failure -> {
                    hideLoading()
                    carePoints?.clear()
                    carePoints?.let { it1 -> carePointsAdapter?.updateCarePoints(it1) }
                    fragmentCarePointsBinding.noCareFoundTV.visibility = View.VISIBLE
                }
            }
        }

        fragmentCarePointsBinding.calendarView.onFocusChangeListener
    }

    private fun openCarePointDetails(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
            findNavController().navigate(
                CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                    it
                )
            )
        }
    }


    private fun setCarePointsAdapter() {
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel, carePoints!!, clickType,this)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        val typeFaceGothamBold = ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
        val typeFaceGothamBook = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        when (p0?.id) {
            R.id.tv_today -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                clickType = CalendarState.Today.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.today)
                val cal = Calendar.getInstance()
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
                onDayClickDataFetch()
                val calendars: ArrayList<Calendar> = ArrayList()
                calendars.add(fragmentCarePointsBinding.calendar.currentPageDate)
                fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
            }
            R.id.tv_week -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                clickType = CalendarState.Week.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.week)
                setColorBasedOnCarePOintsType(
                    fragmentCarePointsBinding.tvWeek,
                    fragmentCarePointsBinding.tvToday,
                    fragmentCarePointsBinding.tvMonth
                )
                val calendars: ArrayList<Calendar> = ArrayList()
                fragmentCarePointsBinding.calendar.showCurrentMonthPage()
                for (i in 0 until 7) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DATE, i)
                    calendars.add(cal)
                }
                fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
                onWeekClickDataFetch()

            }
            R.id.tv_month -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBold
                clickType = CalendarState.Month.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.month)
                val calendars: ArrayList<Calendar> = ArrayList()
                fragmentCarePointsBinding.calendar.showCurrentMonthPage()
                for (i in 0 until 30) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DATE, i)
                    calendars.add(cal)
                }
                fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
                onMonthClickDatFetch()

            }
        }
    }

    private fun setColorBasedOnCarePOintsType(
        selected: AppCompatTextView,
        unSelected: AppCompatTextView,
        unSelectedSecond: AppCompatTextView
    ) {
        selected.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.colorWhite
            )
        )
        unSelected.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.colorLightWhite
            )
        )
        unSelectedSecond.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.colorLightWhite
            )
        )
    }

    private fun onDayClickDataFetch() {
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvMonth
        )
        startDate =
            sdf!!.format(fragmentCarePointsBinding.calendar.currentPageDate.time)
        endDate = sdf!!.format(fragmentCarePointsBinding.calendar.currentPageDate.time)
        getCarePointList(startDate,endDate)
    }

    private fun onWeekClickDataFetch() {
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvMonth
        )
        val calendar = Calendar.getInstance()
        startDate = sdf!!.format(calendar.time)
        calendar.add(Calendar.DATE, 6)
        endDate = sdf!!.format(calendar.time)
        getCarePointList(startDate,endDate)
    }

    private fun onMonthClickDatFetch() {
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvMonth,
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek
        )
        val calendar = Calendar.getInstance()
        calendar.time = fragmentCarePointsBinding.calendar.currentPageDate.time
        startDate = sdf!!.format(calendar.time)
        calendar.add(Calendar.MONTH, 1)
        endDate = sdf!!.format(calendar.time)
        getCarePointList(startDate,endDate)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }

    private fun getCarePointList(startDate:String,endDate:String){
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate,carePointsViewModel.getLovedOneUUId()!!)
    }

    override fun onEventSelected(id: Int) {
        //open event detail page
        findNavController().navigate(
            CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                id
            )
        )
    }
}

