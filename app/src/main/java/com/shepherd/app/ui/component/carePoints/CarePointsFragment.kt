package com.shepherd.app.ui.component.carePoints


import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.data.dto.added_events.ResultEventModel
import com.shepherd.app.databinding.FragmentCarePointsBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.carePoints.adapter.CarePointsDayAdapter
import com.shepherd.app.utils.CalendarState
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.observe
import com.shepherd.app.view_model.CreatedCarePointsViewModel
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.shepherd.app.data.dto.added_events.AddedEventModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Nikita kohli on 26-07-22
 */

@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class CarePointsFragment : BaseFragment<FragmentCarePointsBinding>(),
    View.OnClickListener, CarePointsDayAdapter.EventSelected {
    private lateinit var fragmentCarePointsBinding: FragmentCarePointsBinding
    private var typeFaceGothamBold: Typeface? = null
    private var typeFaceGothamBook: Typeface? = null
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
        fragmentCarePointsBinding.calendarPView.clearSelection()
        return fragmentCarePointsBinding.root
    }

    override fun onResume() {
        super.onResume()
        fragmentCarePointsBinding.calendarPView.clearSelection()
        clickType = CalendarState.Today.value
        fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBold
        fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
        clickType = CalendarState.Today.value
        fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.today)
        fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvMonth
        )
        sdf = SimpleDateFormat("yyyy-MM-dd")
        startDate = sdf!!.format(Calendar.getInstance().time)
        endDate = startDate
        getCarePointList(startDate, endDate)
        fragmentCarePointsBinding.calendarPView.clearSelection()
        val cal = Calendar.getInstance()
        fragmentCarePointsBinding.calendarPView.setDateSelected(cal, true)
    }
    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        typeFaceGothamBold = ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
        typeFaceGothamBook = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        fragmentCarePointsBinding.listener = this

        fragmentCarePointsBinding.calendarPView.setOnDateChangedListener { widget, date, selected ->
            fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
            getDateSelectedOnTypeBased(
                date.date
            )
        }
        fragmentCarePointsBinding.calendarPView.setOnMonthChangedListener { widget, date ->
            when (clickType) {
                CalendarState.Month.value -> {
                    fragmentCarePointsBinding.calendarPView.selectionMode = MaterialCalendarView.SELECTION_MODE_NONE
                    val month = SimpleDateFormat("MM").format(date.date)
                    val year = SimpleDateFormat("yyyy").format(date.date)
                    fragmentCarePointsBinding.textViewSelectGroup.text = SimpleDateFormat("MMM yyyy").format(date.date)
                    fragmentCarePointsBinding.calendarPView.clearSelection()
                    val calendar = Calendar.getInstance()
                    calendar.time = date.date
                    startDate = sdf!!.format(calendar.time)
                    calendar.add(Calendar.DATE, getDayCount(month = month, year = year))
                    endDate = sdf!!.format(calendar.time)
                    getCarePointList(startDate, endDate)
                }
                else -> {
                    fragmentCarePointsBinding.calendarPView.selectionMode =
                        MaterialCalendarView.SELECTION_MODE_SINGLE
                }
            }

        }
    }

    private fun getDateSelectedOnTypeBased(selectedDate: Date) {
        when (clickType) {
            CalendarState.Today.value -> {
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                fragmentCarePointsBinding.calendarPView.clearSelection()
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate
                fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
                startDate = sdf!!.format(selectedDate)
                endDate = sdf!!.format(selectedDate)
                getCarePointList(startDate, endDate)
            }
            CalendarState.Week.value -> {
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate
                startDate = sdf!!.format(calendar.time)
                val startDay = SimpleDateFormat("MMM dd").format(calendar.time)
                calendar.add(Calendar.DATE, 7)
                endDate = sdf!!.format(calendar.time)
                val endDay = SimpleDateFormat("MMM dd").format(calendar.time)
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.week).plus(", ").plus(startDay).plus(" to ").plus(endDay)
                getCarePointList(startDate, endDate)
                for (i in 0 until 7) {
                    val cal = Calendar.getInstance()
                    cal.time = selectedDate
                    cal.add(Calendar.DATE, i)
                    fragmentCarePointsBinding.calendarPView.setDateSelected(cal, true)
                }
            }
            else -> {
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_NONE

            }
        }
    }

    private fun getDayCount(month: String, year: String): Int {
        val days = when (month) {
            "1", "3", "5", "7", "8", "10", "12","01", "03", "05","07","08" -> {
                31
            }
            "4", "6", "9", "11",  "04", "06", "09" -> {
                30
            }
            else -> {
                when {
                    year.toInt() % 400 == 0 || year.toInt() %  4 == 0 -> 29
                    else -> 28
                }
            }
        }
        return days
    }

    override fun observeViewModel() {
//        observe(carePointsViewModel.openMemberDetails, ::openCarePointDetails)
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

    }

    private fun openCarePointDetails(navigateEvent: SingleEvent<Int>) {
        navigateEvent.getContentIfNotHandled()?.let {
/*
            findNavController().navigate(
                CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                    it
                )
            )
*/
        }
    }


    private fun setCarePointsAdapter() {
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel, carePoints!!, clickType, this)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_today -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                clickType = CalendarState.Today.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.today)
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                setColorBasedOnCarePOintsType(
                    fragmentCarePointsBinding.tvToday,
                    fragmentCarePointsBinding.tvWeek,
                    fragmentCarePointsBinding.tvMonth
                )
                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
                startDate = sdf!!.format(calendar.time)
                endDate = sdf!!.format(calendar.time)
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                getCarePointList(startDate, endDate)
            }
            R.id.tv_week -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                clickType = CalendarState.Week.value
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                setColorBasedOnCarePOintsType(
                    fragmentCarePointsBinding.tvWeek,
                    fragmentCarePointsBinding.tvToday,
                    fragmentCarePointsBinding.tvMonth
                )
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                startDate = sdf!!.format(calendar.time)
                val startDay = SimpleDateFormat("MMM dd").format(calendar.time)
                calendar.add(Calendar.DATE, 7)
                endDate = sdf!!.format(calendar.time)
                val endDay = SimpleDateFormat("MMM dd").format(calendar.time)
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.week).plus(", ").plus(startDay).plus(" to ").plus(endDay)
                getCarePointList(startDate, endDate)
                for (i in 0 until 7) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DATE, i)
                    fragmentCarePointsBinding.calendarPView.setDateSelected(cal, true)
                }

            }
            R.id.tv_month -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBold
                clickType = CalendarState.Month.value
                fragmentCarePointsBinding.textViewSelectGroup.text = SimpleDateFormat("MMM yyyy").format(Calendar.getInstance().time)
                setColorBasedOnCarePOintsType(
                    fragmentCarePointsBinding.tvMonth,
                    fragmentCarePointsBinding.tvWeek,
                    fragmentCarePointsBinding.tvToday

                )

                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                val month = SimpleDateFormat("MM").format(calendar.time)
                val year = SimpleDateFormat("yyyy").format(calendar.time)
                val currentMonthDate = "01/$month/$year"
                calendar.time = SimpleDateFormat("dd/MM/yyyy").parse(currentMonthDate)!!
                fragmentCarePointsBinding.calendarPView.setCurrentDate(calendar.timeInMillis)
                startDate = sdf!!.format(calendar.time)
                calendar.add(Calendar.DATE, getDayCount(month, year))
                endDate = sdf!!.format(calendar.time)
                getCarePointList(startDate, endDate)

             /*   for (i in 0 until getDayCount(month, year)) {
                    val cal = calendar
                    cal.add(Calendar.DATE, i)
                    fragmentCarePointsBinding.calendarPView.setDateSelected(cal, true)
                }*/
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
        getCarePointList(startDate, endDate)
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
        getCarePointList(startDate, endDate)
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
        getCarePointList(startDate, endDate)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }

    private fun getCarePointList(startDate: String, endDate: String) {
        carePointsViewModel.getCarePointsByLovedOneId(
            pageNumber,
            limit,
            startDate,
            endDate,
            carePointsViewModel.getLovedOneUUId()!!
        )
    }

    override fun onEventSelected(detail: AddedEventModel) {
        //open event detail page
        findNavController().navigate(
            CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                detail
            )
        )
    }
}

