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
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


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
        getCarePointList(startDate, endDate)
        getCarePointList(startDate, endDate)
//        fragmentCarePointsBinding.calendar.setForwardButtonImage(resources.getDrawable(R.drawable.ic_right_arrow))
//        fragmentCarePointsBinding.calendar.setPreviousButtonImage(resources.getDrawable(R.drawable.ic_left_arrow))

        val calendars: List<Calendar> = ArrayList()
        calendars
//        fragmentCarePointsBinding.calendar.setHighlightedDays(calendars)
        fragmentCarePointsBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            when (clickType) {
                CalendarState.Today.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    endDate = sdf!!.format(calendar.time)
                    getCarePointList(startDate, endDate)
                }
                CalendarState.Week.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    calendar.add(Calendar.DATE, 7)
                    endDate = sdf!!.format(calendar.time)
                    getCarePointList(startDate, endDate)
                }
                CalendarState.Month.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    calendar.add(Calendar.MONTH, 1)
                    endDate = sdf!!.format(calendar.time)
                    getCarePointList(startDate, endDate)
                }
            }
        }
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
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel, carePoints!!, clickType, this)
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
            }
            R.id.tv_week -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                clickType = CalendarState.Week.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.week)
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0
                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)
                cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
                onWeekClickDataFetch()
            }
            R.id.tv_month -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBold
                clickType = CalendarState.Month.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.month)
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0
                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)
                cal[Calendar.DAY_OF_MONTH] = cal.firstDayOfWeek
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
                onMonthClickDatFetch()
            }
        }
    }

    private fun setColorBasedOnCarePointsType(
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
                R.color.colorViewPagerInactive
            )
        )
        unSelectedSecond.setTextColor(
            ContextCompat.getColor(
                requireContext().applicationContext,
                R.color.colorViewPagerInactive
            )
        )
    }

    private fun onDayClickDataFetch() {
        setColorBasedOnCarePointsType(
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvMonth
        )
        startDate =
            sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        endDate = sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        getCarePointList(startDate, endDate)
    }

    private fun onWeekClickDataFetch() {
        setColorBasedOnCarePointsType(
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvMonth
        )
        val calendar = Calendar.getInstance()
        calendar.time = sdf!!.parse(
            sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        )!!
        startDate = sdf!!.format(calendar.time)
        calendar.add(Calendar.DATE, 7)
        endDate = sdf!!.format(calendar.time)
        getCarePointList(startDate, endDate)
    }

    private fun onMonthClickDatFetch() {
        setColorBasedOnCarePointsType(
            fragmentCarePointsBinding.tvMonth,
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek
        )
        val calendar = Calendar.getInstance()
        calendar.time = sdf!!.parse(
            sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        )!!
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

    override fun onEventSelected(id: Int) {
        //open event detail page
        findNavController().navigate(
            CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                id
            )
        )
    }
}

