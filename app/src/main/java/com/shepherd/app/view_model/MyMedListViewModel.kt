package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.data.dto.med_list.GetMedicationRecordResponse
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.med_list.MedListRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.DeleteAddedMedicationResponseModel
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
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

    private var _medicationRecordResponseLiveData =
        MutableLiveData<Event<DataResult<MedicationRecordResponseModel>>>()
    var medicationRecordResponseLiveData: LiveData<Event<DataResult<MedicationRecordResponseModel>>> =
        _medicationRecordResponseLiveData

    private var _getMedicationRecordResponseLiveData =
        MutableLiveData<Event<DataResult<GetMedicationRecordResponse>>>()
    var getMedicationRecordResponseLiveData: LiveData<Event<DataResult<GetMedicationRecordResponse>>> =
        _getMedicationRecordResponseLiveData

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMedDetailItemsPrivate = MutableLiveData<SingleEvent<MedListReminder>>()
    val openMedDetailItems: LiveData<SingleEvent<MedListReminder>> get() = openMedDetailItemsPrivate

    private val medDetailItemsPrivate = MutableLiveData<SingleEvent<Payload>>()
    val medDetailItems: LiveData<SingleEvent<Payload>> get() = medDetailItemsPrivate

    private val _selectedMedicationLiveData = MutableLiveData<SingleEvent<MedListReminder>>()
    val selectedMedicationLiveData: LiveData<SingleEvent<MedListReminder>> get() = _selectedMedicationLiveData

    fun openMedDetail(item: MedListReminder) {
        openMedDetailItemsPrivate.value = SingleEvent(item)
    }

    fun openMedicineDetail(item: Payload) {
        medDetailItemsPrivate.value = SingleEvent(item)
    }

    fun selectedMedication(item: MedListReminder) {
        _selectedMedicationLiveData.value = SingleEvent(item)
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
    fun getLovedOneMedLists(date:String=""): LiveData<Event<DataResult<GetLovedOneMedList>>> {
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
                response.collect {
                    _deletedScheduledMedicationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return deletedScheduledMedicationResponseLiveData
    }

    // Add Medication Record
    fun addUserMedicationRecord(medicationRecordRequestModel: MedicationRecordRequestModel): LiveData<Event<DataResult<MedicationRecordResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.addUserMedicationRecord(medicationRecordRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _medicationRecordResponseLiveData.postValue(Event(it))
                }
            }
        }
        return medicationRecordResponseLiveData
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    // Get Medication Record
    fun getMedicationRecords(
        lovedOneId: String,
        page: Int,
        limit: Int,
        date:String
    ): LiveData<Event<DataResult<GetMedicationRecordResponse>>> {
        viewModelScope.launch {
            val response = medListRepository.getMedicationRecords(lovedOneId, page, limit,date)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getMedicationRecordResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getMedicationRecordResponseLiveData
    }
}
