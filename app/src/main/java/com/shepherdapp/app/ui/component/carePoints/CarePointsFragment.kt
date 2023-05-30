package com.shepherdapp.app.ui.component.carePoints


import CommonFunctions.getLastDayOf
import CommonFunctions.isToday
import CommonFunctions.stringCarePointDateToDate
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.added_events.AddedEventModel
import com.shepherdapp.app.data.dto.added_events.ResultEventModel
import com.shepherdapp.app.data.dto.chat.ChatModel
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.databinding.FragmentCarePointsBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.base.listeners.UpdateViewOfParentListener
import com.shepherdapp.app.ui.component.carePoints.adapter.CarePointsDayAdapter
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nikita Kohli on 26-07-22
 */

@SuppressLint("SimpleDateFormat")
@AndroidEntryPoint
class CarePointsFragment : BaseFragment<FragmentCarePointsBinding>(),
    View.OnClickListener, CarePointsDayAdapter.EventSelected {
    private lateinit var fragmentCarePointsBinding: FragmentCarePointsBinding
    private var typeFaceGothamBold: Typeface? = null
    private var typeFaceGothamBook: Typeface? = null
    private var carePoints: ArrayList<ResultEventModel> = ArrayList()
    private var carePointsAdapter: CarePointsDayAdapter? = null
    private val carePointsViewModel: CreatedCarePointsViewModel by viewModels()
    private var pageNumber: Int = 1
    private var limit: Int = 10
    private var startDate: String = ""
    private var endDate: String = ""
    private var sdf = SimpleDateFormat("yyyy-MM-dd")
    private var chatModelList: ArrayList<ChatModel>? = ArrayList()
    private var selDate: Date? = null
    private var isInitialized = false

    private val TAG = CarePointsFragment::class.java.simpleName

    private var parentActivityListener: ChildFragmentToActivityListener? = null

    private var updateViewOfParentListenerListener: UpdateViewOfParentListener? = null


    private lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
        if (context is ChildFragmentToActivityListener) parentActivityListener = context
        if (context is UpdateViewOfParentListener) updateViewOfParentListenerListener = context
        else throw RuntimeException("$context must implement ChildFragmentToActivityListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::fragmentCarePointsBinding.isInitialized && !Prefs.with(context)
                ?.getString(Const.START_DATE_EVENT).isNullOrEmpty()
        ) {
            isInitialized = true
            startDate = Prefs.with(context)?.getString(Const.START_DATE_EVENT)!!
            endDate = Prefs.with(context)?.getString(Const.END_DATE_EVENT)!!


            getCarePointList(
                Prefs.with(context)?.getString(Const.START_DATE_EVENT)!!,
                Prefs.with(context)?.getString(Const.END_DATE_EVENT)!!,
            )
        } else {

            fragmentCarePointsBinding =
                FragmentCarePointsBinding.inflate(inflater, container, false)
            fragmentCarePointsBinding.calendarPView.clearSelection()

        }


        return fragmentCarePointsBinding.root
    }

    override fun onResume() {
        super.onResume()

        // Check if loggedIn User is CareTeam Leader for selected lovedOne
        if (!isInitialized) {
            checkForEventClickType()
        } else {
            if (eventClickType == CalendarState.Week.value) {
                val calendar = Calendar.getInstance()
                calendar.time = stringCarePointDateToDate(
                    Prefs.with(requireContext())?.getString(Const.START_DATE_EVENT)
                )
                onWeekSelected(calendar)
            } else if (eventClickType == CalendarState.Today.value) {
                setTodayData(
                    stringCarePointDateToDate(
                        Prefs.with(requireContext())?.getString(Const.START_DATE_EVENT)
                    )
                )
            }
        }


        // Update the home activity so that updated lovedOne is shown on the screen
        parentActivityListener?.msgFromChildFragmentToActivity()


    }


    private fun lastDayOfMonth(Y: Int, M: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.of(Y, M, 1).month.length(Year.of(Y).isLeap)
        } else {
            getLastDayOf(M, Y)
        }
    }

    override fun initViewBinding() {
        typeFaceGothamBold = ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
        typeFaceGothamBook = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        fragmentCarePointsBinding.listener = this
        fragmentCarePointsBinding.calendarPView.elevation = 20f

        clickListener()

        if (carePointsViewModel.isLoggedInUserCareTeamLeader() == true) {
            updateViewOfParentListenerListener?.updateViewVisibility(true)
        } else {
            updateViewOfParentListenerListener?.updateViewVisibility(false)
        }


    }

    private fun clickListener() {
        fragmentCarePointsBinding.tvWeek.setOnClickListener(this)
        fragmentCarePointsBinding.tvMonth.setOnClickListener(this)
        fragmentCarePointsBinding.tvToday.setOnClickListener(this)
    }

    private fun checkForEventClickType() {
        when (eventClickType) {
            CalendarState.Week.value -> {
                fragmentCarePointsBinding.tvWeek.performClick()
            }
            CalendarState.Month.value -> {
                fragmentCarePointsBinding.tvMonth.performClick()
            }
            else -> fragmentCarePointsBinding.tvToday.performClick()
        }
        calendarChangeListener()
    }

    private fun calendarChangeListener() {
        fragmentCarePointsBinding.calendarPView.setOnDateChangedListener { widget, date, selected ->
            if (eventClickType == CalendarState.Today.value) {
                setTodayData(date.date)
            } else if (eventClickType == CalendarState.Week.value) {
                val calendar = Calendar.getInstance()
                calendar.time = date.date
                onWeekSelected(calendar)
            }
        }

        fragmentCarePointsBinding.calendarPView.setOnMonthChangedListener { _, date ->
            // Show care point data as per month change from calendar
            Log.d("TAG", "calendarChangeListener: " + eventClickType)
            if (eventClickType == CalendarState.Month.value) {
                selDate = date.date
                val lastDayOfMonth = lastDayOfMonth(date.year, date.month + 1)
                startDate = SimpleDateFormat("yyyy-MM-dd").format(selDate!!)
                endDate =
                    SimpleDateFormat("yyyy-MM-dd").format(selDate!!).dropLast(2) + lastDayOfMonth
                fragmentCarePointsBinding.calendarPView.clearSelection()
                monthSelected()
            } else if (eventClickType == CalendarState.Today.value) {
                if (!isToday(date.date)) {
                    val startDate = SimpleDateFormat("MMM yyyy").format(date.date)
                    fragmentCarePointsBinding.textViewSelectGroup.text = startDate
                } else {
                    fragmentCarePointsBinding.textViewSelectGroup.text =
                        resources.getString(R.string.today)

                }
            }

        }
    }

    private fun setTodayData(date: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = date
        fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
        startDate = sdf.format(calendar.time)
        endDate = sdf.format(calendar.time)
        getCarePointList(startDate, endDate)

        if (!isToday(date)) {
            val startDate = SimpleDateFormat("MMM dd,yyyy").format(date)
            fragmentCarePointsBinding.textViewSelectGroup.text = startDate
        } else {
            fragmentCarePointsBinding.textViewSelectGroup.text =
                resources.getString(R.string.today)

        }
    }


    private fun getDayCount(month: String, year: String): Int {
        val days = when (month) {
            "1", "3", "5", "7", "8", "10", "12", "01", "03", "05", "07", "08" -> {
                31
            }
            "4", "6", "9", "11", "04", "06", "09" -> {
                30
            }
            else -> {
                when {
                    year.toInt() % 400 == 0 || year.toInt() % 4 == 0 -> 29
                    else -> 28
                }
            }
        }
        return days
    }

    override fun observeViewModel() {
        carePointsViewModel.carePointsResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    fragmentCarePointsBinding.noCareFoundTV.visibility = View.GONE
                    carePoints = it.data.payload.results
                    Collections.sort(carePoints, object : Comparator<ResultEventModel?> {
                        var df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                        override fun compare(
                            o1: ResultEventModel?,
                            o2: ResultEventModel?
                        ): Int {
                            return try {
                                df.parse(o1!!.date!!)!!.compareTo(df.parse(o2!!.date!!))
                            } catch (e: Exception) {
                                throw IllegalArgumentException(e)
                            }
                        }
                    })

                    if (carePoints.isEmpty()) return@observeEvent
                    carePointsAdapter?.updateCarePoints(carePoints)

                    when (eventClickType) {
                        CalendarState.Month.value, CalendarState.Week.value -> {
                            val dates: ArrayList<CalendarDay> = arrayListOf()
                            for (i in carePoints) {
                                val calendar = Calendar.getInstance()
                                calendar.time = SimpleDateFormat("yyyy-MM-dd").parse(i.date!!)!!
                                val year = SimpleDateFormat("yyyy").format(calendar.time)
                                val month = SimpleDateFormat("MM").format(calendar.time)
                                val day = SimpleDateFormat("dd").format(calendar.time)
                                val eventDate = CalendarDay.from(
                                    year.toInt(),
                                    month.toInt() - 1,
                                    day.toInt()
                                ) // year, month, date
                                dates.add(eventDate)
                            }
                            fragmentCarePointsBinding.calendarPView.addDecorators(
                                EventDecorator(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color._A26DCB
                                    ), dates
                                )
                            )
                        }
                        else -> {

                        }
                    }
                    setCarePointsAdapter()
                }
                is DataResult.Failure -> {
                    hideLoading()
                    carePoints.clear()
                    carePoints.let { it1 -> carePointsAdapter?.updateCarePoints(it1) }
                    fragmentCarePointsBinding.noCareFoundTV.visibility = View.VISIBLE
                    fragmentCarePointsBinding.noCareFoundTV.text =
                        getString(R.string.no_care_points_found)
                }
            }
        }

    }

    private fun setCarePointsAdapter() {
        carePointsAdapter = CarePointsDayAdapter(carePointsViewModel, carePoints, this)
        fragmentCarePointsBinding.recyclerViewEventDays.adapter = carePointsAdapter
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_today -> {
                fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBold
                fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
                fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook
                eventClickType = CalendarState.Today.value
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                setColorBasedOnCarePointsType(
                    fragmentCarePointsBinding.tvToday,
                    fragmentCarePointsBinding.tvWeek,
                    fragmentCarePointsBinding.tvMonth
                )
                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
                startDate = sdf.format(calendar.time)
                endDate = sdf.format(calendar.time)
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                getCarePointList(startDate, endDate)
                fragmentCarePointsBinding.textViewSelectGroup.text = getString(R.string.today)
            }
            R.id.tv_week -> {
                onWeekSelected(Calendar.getInstance())
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance())

            }
            R.id.tv_month -> {

                val calendar = Calendar.getInstance()
                val month = SimpleDateFormat("MM").format(calendar.time)
                val year = SimpleDateFormat("yyyy").format(calendar.time)


                val currentMonthDate = "01/$month/$year"
                calendar.time = SimpleDateFormat("dd/MM/yyyy").parse(currentMonthDate)!!
                fragmentCarePointsBinding.calendarPView.setCurrentDate(calendar.timeInMillis)
                startDate = sdf.format(calendar.time)
                calendar.add(Calendar.DATE, getDayCount(month, year))
                endDate = startDate.dropLast(2) + lastDayOfMonth(year.toInt(), month.toInt())
                fragmentCarePointsBinding.calendarPView.clearSelection()
                monthSelected()
            }
        }
    }

    private fun onWeekSelected(calendar: Calendar) {

        fragmentCarePointsBinding.calendarPView.clearSelection()
        fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBold
        fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBook

        eventClickType = CalendarState.Week.value

        fragmentCarePointsBinding.calendarPView.selectionMode =
            MaterialCalendarView.SELECTION_MODE_SINGLE

        setColorBasedOnCarePointsType(
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvToday,
            fragmentCarePointsBinding.tvMonth
        )
        val currentDateCalendar = Calendar.getInstance()
        currentDateCalendar.time = calendar.time
        startDate = sdf.format(currentDateCalendar.time)
        val startDay = SimpleDateFormat("MMM dd").format(currentDateCalendar.time)

        currentDateCalendar.add(Calendar.DATE, 6)

        endDate = sdf.format(currentDateCalendar.time)
        val endDay = SimpleDateFormat("MMM dd").format(currentDateCalendar.time)

        fragmentCarePointsBinding.textViewSelectGroup.text =
            getString(R.string.week).plus(", ").plus(startDay).plus(" to ").plus(endDay)


        getCarePointList(startDate, endDate)


        for (i in 0 until 7) {
            if (i != 0)
                calendar.add(Calendar.DATE, 1)
            fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
        }


    }

    private fun monthSelected() {
        fragmentCarePointsBinding.calendarPView.selectionMode =
            MaterialCalendarView.SELECTION_MODE_NONE
        fragmentCarePointsBinding.calendarPView.clearSelection()
        eventClickType = CalendarState.Month.value
        fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBold
        fragmentCarePointsBinding.textViewSelectGroup.text =
            sdf.parse(startDate)?.let { SimpleDateFormat("MMM yyyy").format(it) }
        setColorBasedOnCarePointsType(
            fragmentCarePointsBinding.tvMonth,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvToday

        )

        getCarePointList(startDate, endDate)
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


    override fun getLayoutRes(): Int {
        return R.layout.fragment_care_points
    }

    private fun getCarePointList(startDate: String, endDate: String) {
        fragmentCarePointsBinding.calendarPView.removeDecorators()
        carePointsViewModel.getCarePointsByLovedOneId(
            pageNumber,
            limit,
            startDate,
            endDate,
            carePointsViewModel.getLovedOneUUId()!!
        )
        Prefs.with(context)?.save(Const.START_DATE_EVENT, startDate)
        Prefs.with(context)?.save(Const.END_DATE_EVENT, endDate)
    }

    override fun onEventSelected(detail: AddedEventModel) {
        when (detail.clickType) {
            ClickType.View.value -> {
                // Get Login User's detail
                val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
                    Const.USER_DETAILS,
                    UserProfile::class.java
                )

                val loggedInUserId = loggedInUser?.id
                val loggedInUserName = loggedInUser?.firstname + " " + loggedInUser?.lastname

                Log.d(TAG, "onEventSelected: $detail ")
                val eventId = detail.id
                val eventName = detail.name
                detail.user_assignes.forEach {
                    val receiverName =
                        it.user_details.firstname + " " + it.user_details.lastname
                    val receiverID = it.user_details.id
                    val receiverPicUrl = it.user_details.profilePhoto
                    val documentID = null
                    val chatType = Chat.CHAT_GROUP

                    // Create Chat Model
                    val chatModel = ChatModel(
                        documentID,
                        loggedInUserId,
                        loggedInUserName,
                        receiverID,
                        receiverName,
                        receiverPicUrl,
                        null,
                        chatType,
                        eventName,
                        eventId
                    )
                    chatModelList?.add(chatModel)
                }

                val action = detail.id?.let {
                    CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                        "Care Point",
                        it
                    )
                }
                action?.let { findNavController().navigate(it) }
            }

            ClickType.Edit.value -> {
                val action =
                    CarePointsFragmentDirections.actionNavCarePointsToEditCarePointFragment(
                        carePoint = detail
                    )
                findNavController().navigate(action)
            }
            ClickType.ASSIGNEE.value -> {

                findNavController().navigate(R.id.action_to_assigneeUsersFragment, bundleOf("assignee_user" to detail.user_assignes,"loved_one_id" to detail.loved_one_user_id))
            }
        }
    }

    private fun getDateSelectedOnTypeBased(selectedDate: Date) {
        when (eventClickType) {
            CalendarState.Today.value -> {
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                fragmentCarePointsBinding.calendarPView.clearSelection()
                fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate
                fragmentCarePointsBinding.calendarPView.setDateSelected(calendar, true)
                startDate = sdf.format(selectedDate)
                endDate = sdf.format(selectedDate)
                getCarePointList(startDate, endDate)
            }
            CalendarState.Week.value -> {
                fragmentCarePointsBinding.calendarPView.selectionMode =
                    MaterialCalendarView.SELECTION_MODE_SINGLE
                fragmentCarePointsBinding.calendarPView.clearSelection()
                val calendar = Calendar.getInstance()
                calendar.time = selectedDate
                startDate = sdf.format(calendar.time)
                val startDay = SimpleDateFormat("MMM dd").format(calendar.time)
                calendar.add(Calendar.DATE, 6)
                endDate = sdf.format(calendar.time)
                val endDay = SimpleDateFormat("MMM dd").format(calendar.time)
                fragmentCarePointsBinding.textViewSelectGroup.text =
                    getString(R.string.week).plus(", ").plus(startDay).plus(" to ").plus(endDay)
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
                    MaterialCalendarView.SELECTION_MODE_SINGLE


                val selectedCalendar: Calendar = Calendar.getInstance()
                selectedCalendar.time = selectedDate
                startDate = sdf.format(selectedDate.time)
                endDate = startDate.dropLast(2) + lastDayOfMonth(
                    selectedCalendar.get(Calendar.YEAR),
                    selectedCalendar.get(Calendar.MONTH) + 1
                )
                fragmentCarePointsBinding.calendarPView.setDateSelected(selectedCalendar, true)

                monthSelected()
            }
        }
    }

}

