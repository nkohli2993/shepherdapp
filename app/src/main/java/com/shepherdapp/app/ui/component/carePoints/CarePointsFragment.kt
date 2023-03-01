package com.shepherdapp.app.ui.component.carePoints


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
    private var clickType = CalendarState.Today.value
    private var sdf: SimpleDateFormat? = null
    private var chatModelList: ArrayList<ChatModel>? = ArrayList()
    private var selDate: Date? = null

    private val TAG = "CarePointsFragment"

    private var parentActivityListener: ChildFragmentToActivityListener? = null

    //    private var childSourceToActivityListener: ChildSourceToActivityListener? = null
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
        fragmentCarePointsBinding =
            FragmentCarePointsBinding.inflate(inflater, container, false)
        fragmentCarePointsBinding.calendarPView.clearSelection()
        return fragmentCarePointsBinding.root
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        // Update the home activity so that updated lovedOne is shown on the screen
        parentActivityListener?.msgFromChildFragmentToActivity()
//        childSourceToActivityListener?.childSource(Const.Screen.CARE_POINT)

        // Check if loggedIn User is CareTeam Leader for selected lovedOne
        val lovedOneDetail = carePointsViewModel.getLovedOneDetail()
        val isCareTeamLeader = carePointsViewModel.isLoggedInUserCareTeamLeader()
        val isNewVisible =
            (lovedOneDetail?.careRoles?.slug == CareRole.CareTeamLead.slug) || (isCareTeamLeader == true)

        /* val isNewVisible = when (lovedOneDetail?.careRoles?.slug) {
             CareRole.CareTeamLead.slug -> {
                 true
             }
             else -> {
                 false
             }
         }*/
        if (carePointsViewModel.isLoggedInUserCareTeamLeader() == true) {
            updateViewOfParentListenerListener?.updateViewVisibility(true)
        } else {
            updateViewOfParentListenerListener?.updateViewVisibility(false)
        }

        // updateViewOfParentListenerListener?.updateViewVisibility(isNewVisible)
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

//        fragmentCarePointsBinding.calendarPView.addDecorator(DayDisableDecorator())


        fragmentCarePointsBinding.calendarPView.setOnDateChangedListener { widget, date, selected ->
            selDate = date.date
            Log.d(TAG, " onDateChangeListener :selected date is $selDate")
            Log.d(TAG, " onDateChangeListener1 :selected date is ${date.date}")


            // Get Current date
            val currentDate = Date()
            Log.d(TAG, "current date is :$currentDate ")

            /*  if (selectedDate.before(currentDate)) {
                  Log.d(TAG, "Selected Date is before current date")
  //                showWarning(requireContext(), "You can not view the events before current date...")
                  fragmentCarePointsBinding.noCareFoundTV.let {
                      it.visibility = View.VISIBLE
                      it.text = "You can not view events before current date..."
                  }

              } else {
                  Log.d(TAG, "Selected Date is after current date")
                  fragmentCarePointsBinding.noCareFoundTV.visibility = View.GONE
                  getDateSelectedOnTypeBased(
                      date.date
                  )
              }*/

            // fragmentCarePointsBinding.calendarPView.setCurrentDate(selDate?.time!!)

            getDateSelectedOnTypeBased(
                date.date
            )


        }



        fragmentCarePointsBinding.calendarPView.setOnMonthChangedListener { _, date ->
            // Show care point data as per month change from calendar
            selDate = date.date
            val lastDayOfMonth = lastDayOfMonth(date.year, date.month + 1)
            startDate = SimpleDateFormat("yyyy-MM-dd").format(selDate!!)
            endDate = SimpleDateFormat("yyyy-MM-dd").format(selDate!!).dropLast(2) + lastDayOfMonth
            fragmentCarePointsBinding.calendarPView.clearSelection()
            monthSelected()

        }
    }

    private fun lastDayOfMonth(Y: Int, M: Int): Int {
        return LocalDate.of(Y, M, 1).month.length(Year.of(Y).isLeap)
    }

    @SuppressLint("SimpleDateFormat")
    override fun initViewBinding() {
        Log.d(TAG, "initViewBinding: ")
        typeFaceGothamBold = ResourcesCompat.getFont(requireContext(), R.font.gotham_bold)
        typeFaceGothamBook = ResourcesCompat.getFont(requireContext(), R.font.gotham_book)
        fragmentCarePointsBinding.listener = this
        fragmentCarePointsBinding.calendarPView.clearSelection()
        clickType = CalendarState.Today.value

        fragmentCarePointsBinding.calendarPView.elevation = 20f


        /* fragmentCarePointsBinding.calendarPView.setOnDateChangedListener { widget, date, selected ->
             Log.d(TAG, " date is ${date.date}")

             fragmentCarePointsBinding.calendarPView.setCurrentDate(Calendar.getInstance().timeInMillis)

             getDateSelectedOnTypeBased(
                 date.date
             )
         }*/

        /*  fragmentCarePointsBinding.calendarPView.setOnMonthChangedListener { _, date ->
              // Show care point data as per month change from calendar
              Log.d(TAG, "initViewBinding: date is ${date.date}")
              val month = SimpleDateFormat("MM").format(date.date)
              val year = SimpleDateFormat("yyyy").format(date.date)
              fragmentCarePointsBinding.textViewSelectGroup.text =
                  SimpleDateFormat("MMM yyyy").format(date.date)
              fragmentCarePointsBinding.calendarPView.clearSelection()
              val calendar = Calendar.getInstance()
              calendar.time = date.date
              startDate = sdf!!.format(calendar.time)
              calendar.add(Calendar.DATE, getDayCount(month = month, year = year))
              endDate = sdf!!.format(calendar.time)
              getCarePointList(startDate, endDate)

              when (clickType) {
                  CalendarState.Month.value -> {
                      fragmentCarePointsBinding.calendarPView.selectionMode =
                          MaterialCalendarView.SELECTION_MODE_NONE
                      val month = SimpleDateFormat("MM").format(date.date)
                      val year = SimpleDateFormat("yyyy").format(date.date)
                      fragmentCarePointsBinding.textViewSelectGroup.text =
                          SimpleDateFormat("MMM yyyy").format(date.date)
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

          }*/
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
                calendar.add(Calendar.DATE, 6)
                endDate = sdf!!.format(calendar.time)
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
                    MaterialCalendarView.SELECTION_MODE_NONE

            }
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
                    Collections.sort(carePoints, object : Comparator<ResultEventModel?> {
                        var df: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                        override fun compare(o1: ResultEventModel?, o2: ResultEventModel?): Int {
                            return try {
                                df.parse(o1!!.date!!)!!.compareTo(df.parse(o2!!.date!!))
                            } catch (e: Exception) {
                                throw IllegalArgumentException(e)
                            }
                        }
                    })

                    /* val date = selDate.toString().changeDateFormat(
                         sourceDateFormat = "EEE MMM dd HH:mm:ss zzz yyyy",
                         targetDateFormat = "MM"
                     )
                     Log.d(TAG, "observeViewModel: Selected Month is $date")*/

                    /* if (selDate?.before(Date()) == true) {
                         fragmentCarePointsBinding.noCareFoundTV.visibility = View.VISIBLE
                         fragmentCarePointsBinding.noCareFoundTV.text =
                             "You can not view the CarePoints before current date"
                     } else {
                         if (carePoints.isEmpty()) return@observeEvent
                         carePointsAdapter?.updateCarePoints(carePoints)
                     }*/
                    if (carePoints.isEmpty()) return@observeEvent
                    carePointsAdapter?.updateCarePoints(carePoints)

                    when (clickType) {
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
                    /* if (selDate?.before(Date()) == true) {
                         fragmentCarePointsBinding.noCareFoundTV.visibility = View.VISIBLE
                         fragmentCarePointsBinding.noCareFoundTV.text =
                             "You can not view the CarePoints before current date"
                     } else {
                         fragmentCarePointsBinding.noCareFoundTV.visibility = View.GONE
                         setCarePointsAdapter()
                     }*/
                    setCarePointsAdapter()
                }
                is DataResult.Failure -> {
                    hideLoading()
                    carePoints.clear()
                    carePoints.let { it1 -> carePointsAdapter?.updateCarePoints(it1) }
                    fragmentCarePointsBinding.noCareFoundTV.visibility = View.VISIBLE
                    fragmentCarePointsBinding.noCareFoundTV.text =
                        getString(R.string.no_care_points_found)
                    /* if (selDate?.before(Date()) == true) {
                         fragmentCarePointsBinding.noCareFoundTV.text =
                             "You can not view the CarePoints before current date"
                     } else {
                         fragmentCarePointsBinding.noCareFoundTV.text =
                             getString(R.string.no_care_points_found)
                     }*/

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
                calendar.add(Calendar.DATE, 6)
                endDate = sdf!!.format(calendar.time)
                val endDay = SimpleDateFormat("MMM dd").format(calendar.time)
                fragmentCarePointsBinding.textViewSelectGroup.text =
                    getString(R.string.week).plus(", ").plus(startDay).plus(" to ").plus(endDay)
                getCarePointList(startDate, endDate)
                for (i in 0 until 7) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DATE, i)
                    fragmentCarePointsBinding.calendarPView.setDateSelected(cal, true)
                }

            }
            R.id.tv_month -> {
                val calendar = Calendar.getInstance()
                val month = SimpleDateFormat("MM").format(calendar.time)
                val year = SimpleDateFormat("yyyy").format(calendar.time)
                val currentMonthDate = "01/$month/$year"
                calendar.time = SimpleDateFormat("dd/MM/yyyy").parse(currentMonthDate)!!
                fragmentCarePointsBinding.calendarPView.setCurrentDate(calendar.timeInMillis)
                startDate = sdf!!.format(calendar.time)
                calendar.add(Calendar.DATE, getDayCount(month, year))
                endDate = startDate.dropLast(2)+lastDayOfMonth(year.toInt(),month.toInt())
                fragmentCarePointsBinding.calendarPView.clearSelection()
                monthSelected()
            }
        }
    }

    private fun monthSelected() {
        fragmentCarePointsBinding.tvToday.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvWeek.typeface = typeFaceGothamBook
        fragmentCarePointsBinding.tvMonth.typeface = typeFaceGothamBold
        clickType = CalendarState.Month.value
        fragmentCarePointsBinding.textViewSelectGroup.text =
            sdf?.parse(startDate)?.let { SimpleDateFormat("MMM yyyy").format(it) }
        setColorBasedOnCarePOintsType(
            fragmentCarePointsBinding.tvMonth,
            fragmentCarePointsBinding.tvWeek,
            fragmentCarePointsBinding.tvToday

        )

        getCarePointList(startDate, endDate)
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
        fragmentCarePointsBinding.calendarPView.removeDecorators()
        carePointsViewModel.getCarePointsByLovedOneId(
            pageNumber,
            limit,
            startDate,
            endDate,
            carePointsViewModel.getLovedOneUUId()!!
        )
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
                val eventLocation = detail.location
                val eventDate = detail.date
                val eventTime = detail.time
                detail.user_assignes.forEach {
                    val receiverName = it.user_details.firstname + " " + it.user_details.lastname
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

                //open event detail page
                /* findNavController().navigate(
                     detail?.id?.let {
                         CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                             "Care Point",
                             it
                         )
                     }
                 )*/

                val action = detail.id?.let {
                    CarePointsFragmentDirections.actionCarePointsToDetailFragment(
                        "Care Point",
                        it
                    )
                }
                action?.let { findNavController().navigate(it) }

                // Navigate to chat screen
                /* findNavController().navigate(
                     CarePointsFragmentDirections.actionNavCarePointsToNavChat(
                         "CarePoint",
                         chatModelList?.toTypedArray()
                     )
                 )*/
            }

            ClickType.Edit.value -> {
                val action =
                    CarePointsFragmentDirections.actionNavCarePointsToEditCarePointFragment(
                        carePoint = detail
                    )
                findNavController().navigate(action)
            }
        }
    }
}

