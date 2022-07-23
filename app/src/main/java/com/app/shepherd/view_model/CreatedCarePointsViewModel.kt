package com.app.shepherd.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.added_events.*
import com.app.shepherd.data.dto.dashboard.LoveUser
import com.app.shepherd.data.dto.login.UserProfile
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.care_point.CarePointRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class CreatedCarePointsViewModel @Inject constructor(
    private val carePointRepository: CarePointRepository,
    private val userRepository: UserRepository,
) :
    BaseViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val addedCarePointLiveData = MutableLiveData<SingleEvent<Int>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openChatItemsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openMemberDetails: LiveData<SingleEvent<Int>> get() = addedCarePointLiveData

    // for care point listing
    private var _addedCarePointLiveData =
        MutableLiveData<Event<DataResult<AddedEventResponseModel>>>()
    var carePointsResponseLiveData: LiveData<Event<DataResult<AddedEventResponseModel>>> =
        _addedCarePointLiveData

    // for care point event detail
    private var _addedCarePointDetailLiveData =
        MutableLiveData<Event<DataResult<EventDetailResponseModel>>>()
    var carePointsResponseDetailLiveData: LiveData<Event<DataResult<EventDetailResponseModel>>> =
        _addedCarePointDetailLiveData

    // for care point event detail comments
    private var _addedCarePointDetailCommentsLiveData =
        MutableLiveData<Event<DataResult<AllCommentEventsResponseModel>>>()
    var addedCarePointDetailCommentsLiveData: LiveData<Event<DataResult<AllCommentEventsResponseModel>>> =
        _addedCarePointDetailCommentsLiveData

    //for comment
    private var _addedCarePointCommentLiveData =
        MutableLiveData<Event<DataResult<EventCommentResponseModel>>>()
    var addedCarePointCommentLiveData: LiveData<Event<DataResult<EventCommentResponseModel>>> =
        _addedCarePointCommentLiveData


    fun getCarePointsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        start_date: String,
        end_date: String, loved_one_user_uid: String
    ): LiveData<Event<DataResult<AddedEventResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getCarePointsAdded(
                    pageNumber,
                    limit,
                    start_date,
                    end_date,
                    loved_one_user_uid
                )
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointLiveData.postValue(Event(it)) }
            }
        }

        return carePointsResponseLiveData
    }

    fun getCarePointsDetailId(
        id: Int
    ): LiveData<Event<DataResult<EventDetailResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getCarePointsDetailIdBased(id)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointDetailLiveData.postValue(Event(it)) }
            }
        }
        return carePointsResponseDetailLiveData
    }

    fun getCarePointsEventCommentsId(
        page: Int, limit: Int, id: Int
    ): LiveData<Event<DataResult<AllCommentEventsResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getEventCommentsIdBased(page, limit, id)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointDetailCommentsLiveData.postValue(Event(it)) }
            }
        }
        return addedCarePointDetailCommentsLiveData
    }

    fun addEventCommentCarePoint(
        eventCommentModel: EventCommentModel
    ): LiveData<Event<DataResult<EventCommentResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.addEventComment(eventCommentModel)
            withContext(Dispatchers.Main) {
                response.collect { _addedCarePointCommentLiveData.postValue(Event(it)) }
            }
        }
        return addedCarePointCommentLiveData
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun openEventChat(item: Int) {
        openChatItemsPrivate.value = SingleEvent(item)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")
    fun getLovedOneId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_ID, "")

    //get userinfo from Shared Pref
    fun getLovedUserDetail(): LoveUser? {
        return userRepository.getLovedUser()
    }
    //get userinfo from Shared Pref
    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }
}
