package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.med_list.AddScheduledMedicationResponseModel
import com.shepherd.app.data.dto.med_list.GetAllDoseListResponseModel
import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.schedule_medlist.DoseList
import com.shepherd.app.data.dto.med_list.ScheduledMedicationRequestModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.med_list.MedListRepository
import com.shepherd.app.network.retrofit.DataResult
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
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val medListRepository: MedListRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {
    // selected med list data
    private val _selectedMedicationDetail = MutableLiveData<SingleEvent<Int>>()
    val selectedMedicationDetail: LiveData<SingleEvent<Int>> get() = _selectedMedicationDetail

    // selected time list position
    private val _timeSelectedlist = MutableLiveData<SingleEvent<Int>>()
    val timeSelectedlist: LiveData<SingleEvent<Int>> get() = _timeSelectedlist

    // selected dost data
    private val _doseListData = MutableLiveData<SingleEvent<DoseList>>()
    val doseListData: LiveData<SingleEvent<DoseList>> get() = _doseListData

    // selected dost data
    private val _dayListSelectedData = MutableLiveData<SingleEvent<Int>>()
    val dayListSelectedData: LiveData<SingleEvent<Int>> get() = _dayListSelectedData

    private var _getMedListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllMedListResponseModel>>>()
    var getMedListResponseLiveData: LiveData<Event<DataResult<GetAllMedListResponseModel>>> =
        _getMedListResponseLiveData
// search med list
    private var _searchMedListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllMedListResponseModel>>>()
    var searchMedListResponseLiveData: LiveData<Event<DataResult<GetAllMedListResponseModel>>> =
        _searchMedListResponseLiveData

    // get all dose list
    private var _getDoseListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllDoseListResponseModel>>>()
    var getDoseListResponseLiveData: LiveData<Event<DataResult<GetAllDoseListResponseModel>>> =
        _getDoseListResponseLiveData


    // add medication for loved ones
    private var _addScheduledMedicationResponseLiveData =
        MutableLiveData<Event<DataResult<AddScheduledMedicationResponseModel>>>()
    var addScheduledMedicationResponseLiveData: LiveData<Event<DataResult<AddScheduledMedicationResponseModel>>> =
        _addScheduledMedicationResponseLiveData



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
    // Get All MedLists
    fun searchMedList(
        pageNumber: Int,
        limit: Int,
        search:String
    ): LiveData<Event<DataResult<GetAllMedListResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.searchMedList(pageNumber, limit,search)
            withContext(Dispatchers.Main) {
                response.collect {
                    _searchMedListResponseLiveData.postValue(Event(it))
                }
            }
        }
        return searchMedListResponseLiveData

    }

    // Get All MedLists
    fun getAllDoseList(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<GetAllDoseListResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.getAllDoseList(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getDoseListResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getDoseListResponseLiveData

    }

    fun openScheduleMedication(medlistPosition: Int) {
        _selectedMedicationDetail.value = SingleEvent(medlistPosition)
    }

    fun setSelectedTime(timeSelectedlist: Int) {
        _timeSelectedlist.value = SingleEvent(timeSelectedlist)
    }

    fun setSelectedDose(doseList: DoseList) {
        _doseListData.value = SingleEvent(doseList)
    }
    fun setDayData(dayList: Int) {
        _dayListSelectedData.value = SingleEvent(dayList)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }


    // add scheduled medication for loved ones
    fun addScheduledMedication(scheduledMedication: ScheduledMedicationRequestModel): LiveData<Event<DataResult<AddScheduledMedicationResponseModel>>> {
        viewModelScope.launch {
            val response = scheduledMedication.let { medListRepository.addScheduledMedication(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _addScheduledMedicationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addScheduledMedicationResponseLiveData
    }


}
