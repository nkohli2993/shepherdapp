package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.med_list.MedListRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.DeleteAddedMedicationResponseModel
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created By Deepak Rattan
 */
@HiltViewModel
class MyMedListViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val medListRepository: MedListRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var _getMedListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllMedListResponseModel>>>()
    var getMedListResponseLiveData: LiveData<Event<DataResult<GetAllMedListResponseModel>>> =
        _getMedListResponseLiveData

    private var _getLovedOneMedListsResponseLiveData =
        MutableLiveData<Event<DataResult<GetLovedOneMedList>>>()
    var getLovedOneMedListsResponseLiveData: LiveData<Event<DataResult<GetLovedOneMedList>>> =
        _getLovedOneMedListsResponseLiveData

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMedDetailItemsPrivate = MutableLiveData<SingleEvent<MedListReminder>>()
    val openMedDetailItems: LiveData<SingleEvent<MedListReminder>> get() = openMedDetailItemsPrivate

    fun openMedDetail(item: MedListReminder) {
        openMedDetailItemsPrivate.value = SingleEvent(item)
    }


    // delete medication for loved ones
    private var _deletedScheduledMedicationResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteAddedMedicationResponseModel>>>()
    var deletedScheduledMedicationResponseLiveData: LiveData<Event<DataResult<DeleteAddedMedicationResponseModel>>> =
        _deletedScheduledMedicationResponseLiveData



    // Get All MedLists
    fun getAllMedLists(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<GetAllMedListResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.getAllMedLists(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getMedListResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getMedListResponseLiveData

    }

    // Get MedLists of loved one
    fun getLovedOneMedLists(): LiveData<Event<DataResult<GetLovedOneMedList>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUID?.let { medListRepository.getLovedOneMedLists(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _getLovedOneMedListsResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getLovedOneMedListsResponseLiveData
    }

    // add scheduled medication for loved ones
    fun deletedSceduledMedication(scheduledMedicationId: Int): LiveData<Event<DataResult<DeleteAddedMedicationResponseModel>>> {
        viewModelScope.launch {
            val response =
                scheduledMedicationId.let { medListRepository.deletedSceduledMedication(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _deletedScheduledMedicationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return deletedScheduledMedicationResponseLiveData
    }

}
