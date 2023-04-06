package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherdapp.app.data.dto.med_list.GetMedicationRecordResponse
import com.shepherdapp.app.data.dto.med_list.loved_one_med_list.*
import com.shepherdapp.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherdapp.app.data.dto.med_list.medication_record.MedicationRecordResponseModel
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.DeleteAddedMedicationResponseModel
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
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
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
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
    private val openMedDetailItemsPrivate =
        MutableLiveData<SingleEvent<UserMedicationRemiderData>>()
    val openMedDetailItems: LiveData<SingleEvent<UserMedicationRemiderData>> get() = openMedDetailItemsPrivate

    private val medDetailItemsPrivate = MutableLiveData<SingleEvent<UserMedicationData>>()
    val medDetailItems: LiveData<SingleEvent<UserMedicationData>> get() = medDetailItemsPrivate

    private val _selectedMedicationLiveData =
        MutableLiveData<SingleEvent<UserMedicationRemiderData>>()
    val selectedMedicationLiveData: LiveData<SingleEvent<UserMedicationRemiderData>> get() = _selectedMedicationLiveData

    fun openMedDetail(item: UserMedicationRemiderData) {
        openMedDetailItemsPrivate.value = SingleEvent(item)
    }

    fun openMedicineDetail(item: UserMedicationData) {
        medDetailItemsPrivate.value = SingleEvent(item)
    }

    fun selectedMedication(item: UserMedicationRemiderData) {
        _selectedMedicationLiveData.value = SingleEvent(item)
    }

    // delete medication for loved ones
    private var _deletedScheduledMedicationResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteAddedMedicationResponseModel>>>()
    var deletedScheduledMedicationResponseLiveData: LiveData<Event<DataResult<DeleteAddedMedicationResponseModel>>> =
        _deletedScheduledMedicationResponseLiveData

    private var _userDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailByUUIDLiveData


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
    fun getLovedOneMedLists(date: String = ""): LiveData<Event<DataResult<GetLovedOneMedList>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUID?.let { medListRepository.getLovedOneMedLists(it/*,date*/) }
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
        date: String
    ): LiveData<Event<DataResult<GetMedicationRecordResponse>>> {
        viewModelScope.launch {
            val response = medListRepository.getMedicationRecords(lovedOneId, page, limit, date)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getMedicationRecordResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getMedicationRecordResponseLiveData
    }

    // Get User Details
    fun getUserDetailByUUID(): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
        val uuid = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = uuid?.let { authRepository.getUserDetailsByUUID(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _userDetailByUUIDLiveData.postValue(Event(it))
                }
            }
        }
        return userDetailByUUIDLiveData
    }

    fun getLovedOneDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }

    fun isLoggedInUserCareTeamLeader(): Boolean? {
        return userRepository.isLoggedInUserTeamLead()
    }
    fun isMedListPermission(): Boolean? {
        return userRepository.isMedListPermission()
    }

}
