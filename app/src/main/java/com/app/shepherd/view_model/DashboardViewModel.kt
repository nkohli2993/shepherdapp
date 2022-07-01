package com.app.shepherd.view_model

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.R
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.data.dto.dashboard.HomeResponseModel
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.care_teams.CareTeamsRepository
import com.app.shepherd.data.remote.home_repository.HomeRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private val TAG = "DashboardViewModel"
    var dashboardItemList: ArrayList<DashboardModel> = ArrayList()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openDashboardItemsPrivate = MutableLiveData<SingleEvent<DashboardModel>>()
    val openDashboardItems: LiveData<SingleEvent<DashboardModel>> get() = openDashboardItemsPrivate


    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData


    private var _homeResponseLiveData =
        MutableLiveData<Event<DataResult<HomeResponseModel>>>()
    var homeResponseLiveData: LiveData<Event<DataResult<HomeResponseModel>>> =
        _homeResponseLiveData


    fun openDashboardItems(item: DashboardModel) {
        openDashboardItemsPrivate.value = SingleEvent(item)
    }

    fun inflateDashboardList(context: Context) {
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_group)!!,
                title = context.getString(R.string.care_team),
                subTitle = context.getString(R.string.members),
                membersCount = "5",
                showImages = true,
                showTasks = false,
                taskCount = "",
                description = "",
                buttonText = context.getString(R.string.view_members),
                colorCode = ContextCompat.getColor(context, R.color.colorAccentDark)
            )
        )
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_calendar)!!,
                title = context.getString(R.string.care_points),
                subTitle = context.getString(R.string.discussions),
                membersCount = "0",
                showImages = false,
                showTasks = true,
                taskCount = "0",
                description = context.getString(R.string.no_events),
                buttonText = context.getString(R.string.view_care_points),
                colorCode = ContextCompat.getColor(context, R.color.colorPeach)
            )
        )
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_file_copy)!!,
                title = context.getString(R.string.lock_box),
                subTitle = context.getString(R.string.files_in_your_lockbox),
                membersCount = "0",
                showImages = false,
                showTasks = false,
                taskCount = "0",
                description = context.getString(R.string.no_documents_yet),
                buttonText = context.getString(R.string.view_documents),
                colorCode = ContextCompat.getColor(context, R.color.colorGreen)
            )
        )
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_medlist)!!,
                title = context.getString(R.string.medlist),
                subTitle = context.getString(R.string.medications_for_today),
                membersCount = "0",
                showImages = false,
                showTasks = false,
                taskCount = "0",
                description = context.getString(R.string.no_dosage),
                buttonText = context.getString(R.string.view_medications),
                colorCode = ContextCompat.getColor(context, R.color.colorPurple)
            )
        )
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_chat_bubble)!!,
                title = context.getString(R.string.messages),
                subTitle = context.getString(R.string.unread_messages),
                membersCount = "1",
                showImages = false,
                showTasks = false,
                taskCount = "0",
                description = context.getString(R.string.last_message_from),
                buttonText = context.getString(R.string.view_messages),
                colorCode = ContextCompat.getColor(context, R.color.colorOrange)
            )
        )
        dashboardItemList.add(
            DashboardModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_resources)!!,
                title = context.getString(R.string.resources),
                subTitle = context.getString(R.string.new_article_and_topics),
                membersCount = "20",
                showImages = false,
                showTasks = false,
                taskCount = "0",
                description = context.getString(R.string.last_article_published),
                buttonText = context.getString(R.string.view_resources),
                colorCode = ContextCompat.getColor(context, R.color.colorYellow)
            )
        )
    }

    fun getCareTeams(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                careTeamsRepository.getCareTeamsByLovedOneId(pageNumber, limit, status, lovedOneId)
            withContext(Dispatchers.Main) {
                response.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }


    fun getHomeData(): LiveData<Event<DataResult<HomeResponseModel>>> {
        val lovedOneId = userRepository.getLovedOneId()
        val status = 1
        Log.d(TAG, "LovedOneID :$lovedOneId ")
        viewModelScope.launch {
            val response = homeRepository.getHomeData(lovedOneId, status)
            withContext(Dispatchers.Main) {
                response.collect {
                    _homeResponseLiveData.postValue(Event(it))
                }
            }
        }
        return homeResponseLiveData
    }
}