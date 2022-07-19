package com.app.shepherd.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.added_events.AddedEventModel
import com.app.shepherd.data.dto.added_events.AddedEventResponseModel
import com.app.shepherd.data.remote.care_point.CarePointRepository
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
class CreatedCarePointsViewModel @Inject constructor(
    private val carePointRepository: CarePointRepository
) :
    BaseViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val addedCarePointLiveData = MutableLiveData<SingleEvent<AddedEventModel>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openChatItemsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openChatItems: LiveData<SingleEvent<Int>> get() = openChatItemsPrivate

    val openMemberDetails: LiveData<SingleEvent<AddedEventModel>> get() = addedCarePointLiveData
    fun openCreatedCarePoints(addedEvent: AddedEventModel) {
        addedCarePointLiveData.value = SingleEvent(addedEvent)
    }


    private var _addedCarePointLiveData =
        MutableLiveData<Event<DataResult<AddedEventResponseModel>>>()
    var carePointsResponseLiveData: LiveData<Event<DataResult<AddedEventResponseModel>>> =
        _addedCarePointLiveData


    fun getCarePointsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        start_date: String,
        end_date: String
    ): LiveData<Event<DataResult<AddedEventResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                carePointRepository.getCarePointsAdded(pageNumber, limit, start_date, end_date)
            withContext(Dispatchers.Main) {
                response?.collect { _addedCarePointLiveData.postValue(Event(it)) }
            }
        }
        return carePointsResponseLiveData
    }
    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun openEventChat(item: Int) {
        openChatItemsPrivate.value = SingleEvent(item)
    }


}
