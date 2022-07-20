package com.app.shepherd.ui.component.carePoints

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
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
    View.OnClickListener {
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
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate)
        fragmentCarePointsBinding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            when (clickType) {
                CalendarState.Today.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    endDate = sdf!!.format(calendar.time)
                    carePointsViewModel.getCarePointsByLovedOneId(
                        pageNumber,
                        limit,
                        startDate,
                        endDate
                    )
                }
                CalendarState.Week.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    calendar.add(Calendar.DATE, 7)
                    endDate = sdf!!.format(calendar.time)
                    carePointsViewModel.getCarePointsByLovedOneId(
                        pageNumber,
                        limit,
                        startDate,
                        endDate
                    )
                }
                CalendarState.Month.value -> {
                    val calendar = GregorianCalendar(year, month, dayOfMonth)
                    startDate = sdf!!.format(calendar.time)
                    calendar.add(Calendar.MONTH, 1)
                    endDate = sdf!!.format(calendar.time)
                    carePointsViewModel.getCarePointsByLovedOneId(
                        pageNumber,
                        limit,
                        startDate,
                        endDate
                    )
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
                    val builder = AlertDialog.Builder(requireContext())
                    val dialog = builder.apply {
                        setTitle("Care Points")
                        setMessage("No Care points found")
                        setPositiveButton("OK") { _, _ ->
                            // navigateToDashboardScreen()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
                }
            }
        }

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
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel, carePoints!!, clickType)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_today -> {
                clickType = CalendarState.Today.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.today)
                val cal = Calendar.getInstance()
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
                onDayClickDataFetch()
            }
            R.id.tv_week -> {
                clickType = CalendarState.Week.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.week)
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !
                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)
                cal[Calendar.DAY_OF_WEEK] = cal.firstDayOfWeek
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
                onWeekClickDataFetch()
            }
            R.id.tv_month -> {
                clickType = CalendarState.Month.value
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.month)
                val cal = Calendar.getInstance()
                cal[Calendar.HOUR_OF_DAY] = 0 // ! clear would not reset the hour of day !
                cal.clear(Calendar.MINUTE)
                cal.clear(Calendar.SECOND)
                cal.clear(Calendar.MILLISECOND)
                cal[Calendar.DAY_OF_MONTH] = cal.firstDayOfWeek
                fragmentCarePointsBinding.calendarView.date = cal.timeInMillis
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
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvMonth
        )
        startDate =
            sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        endDate = sdf!!.format(fragmentCarePointsBinding.calendarView.date)
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate)
    }

    private fun onWeekClickDataFetch() {
        setColorBasedOnCarePOintsType(
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
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate)
    }

    private fun onMonthClickDatFetch() {
        setColorBasedOnCarePOintsType(
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
        carePointsViewModel.getCarePointsByLovedOneId(pageNumber, limit, startDate, endDate)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }
}

