package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventModel
import com.shepherdapp.app.ui.component.addNewEvent.CreateEventResponseModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddNewEventViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponseModel>>()
    val loginLiveData: LiveData<Resource<LoginResponseModel>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _eventMemberLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var eventMemberLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _eventMemberLiveData

    private var _createEventLiveData =
        MutableLiveData<Event<DataResult<CreateEventResponseModel>>>()
    var createEventLiveData: LiveData<Event<DataResult<CreateEventResponseModel>>> =
        _createEventLiveData

    var createEventData = MutableLiveData<CreateEventModel>().apply {
        value = CreateEventModel()
    }


    fun getMembers(
        pageNumber: Int,
        limit: Int,
        status: Int,
        lovedOneId: String?
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getMembers(pageNumber, limit, status, lovedOneId)
            withContext(Dispatchers.Main) {
                response.collect {
                    _eventMemberLiveData.postValue(Event(it))
                }
            }
        }
        return eventMemberLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun createEvent(
        loved_one_user_id: String?,
        name: String,
        location: String,
        date: String,
        time: String,
        notes: String?,
        assign_to: ArrayList<String>,
        repeat_flag: String?,
        repeat_end_date: String?,
        week_days: ArrayList<Int>?,
        month_dates: ArrayList<Int>?,
    ): LiveData<Event<DataResult<CreateEventResponseModel>>> {
        createEventData.value.let {
            it?.loved_one_user_id = loved_one_user_id
            it?.name = name
            it?.location = location.ifEmpty { null }
            it?.date = date
            it?.time = time
            it?.notes = notes
            it?.assign_to = assign_to
            it?.repeat_flag= repeat_flag
            it?.repeat_end_date= repeat_end_date
            it?.week_days = week_days
            it?.month_dates = month_dates

        }


        viewModelScope.launch {
            val response = createEventData.value?.let { dataRepository.createEvent(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _createEventLiveData.postValue(Event(it))
                }
            }
        }
        return createEventLiveData
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }

}
