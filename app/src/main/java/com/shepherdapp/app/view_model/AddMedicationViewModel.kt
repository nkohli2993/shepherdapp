package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.med_list.*
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddMedListRequestModel
import com.shepherdapp.app.data.dto.med_list.add_med_list.AddedMedlistResponseModel
import com.shepherdapp.app.data.dto.med_list.get_medication_detail.GetMedicationDetailResponse
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.DoseList
import com.shepherdapp.app.data.dto.med_list.schedule_medlist.TimePickerData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.network.retrofit.DataResult
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
    private val _timeSelectedlist = MutableLiveData<SingleEvent<TimePickerData>>()
    val timeSelectedlist: LiveData<SingleEvent<TimePickerData>> get() = _timeSelectedlist

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

    // get all dose list
    private var _getDoseTypeListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllDoseListResponseModel>>>()
    var getDoseTypeListResponseLiveData: LiveData<Event<DataResult<GetAllDoseListResponseModel>>> =
        _getDoseTypeListResponseLiveData


    // add medication for loved ones
    private var _addScheduledMedicationResponseLiveData =
        MutableLiveData<Event<DataResult<AddScheduledMedicationResponseModel>>>()
    var addScheduledMedicationResponseLiveData: LiveData<Event<DataResult<AddScheduledMedicationResponseModel>>> =
        _addScheduledMedicationResponseLiveData

    // add medicine
    private var _addMedicineResponseLiveData =
        MutableLiveData<Event<DataResult<AddedMedlistResponseModel>>>()
    var addMedicineResponseLiveData: LiveData<Event<DataResult<AddedMedlistResponseModel>>> =
        _addMedicineResponseLiveData

    private var _getMedicationDetailResponseLiveData =
        MutableLiveData<Event<DataResult<GetMedicationDetailResponse>>>()

    var getMedicationDetailResponseLiveData: LiveData<Event<DataResult<GetMedicationDetailResponse>>> =
        _getMedicationDetailResponseLiveData

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
        search: String
    ): LiveData<Event<DataResult<GetAllMedListResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.searchMedList(pageNumber, limit, search)
            withContext(Dispatchers.Main) {
                response.collect {
                    _searchMedListResponseLiveData.postValue(Event(it))
                }
            }
        }
        return searchMedListResponseLiveData

    }

    // Get All  dose list
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
    // Get All dose type list
    fun getAllDoseTypeList(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<GetAllDoseListResponseModel>>> {
        viewModelScope.launch {
            val response = medListRepository.getAllDoseTypeList(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getDoseTypeListResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getDoseTypeListResponseLiveData

    }

    fun openScheduleMedication(medlistPosition: Int) {
        _selectedMedicationDetail.value = SingleEvent(medlistPosition)
    }

    fun setSelectedTime(timeSelectedlist: TimePickerData) {
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
  // add scheduled medication for loved ones
    fun updateScheduledMedication(scheduledMedication: UpdateScheduledMedList, id:Int): LiveData<Event<DataResult<AddScheduledMedicationResponseModel>>> {
        viewModelScope.launch {
            val response = scheduledMedication.let { medListRepository.updateScheduledMedication(id,it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _addScheduledMedicationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addScheduledMedicationResponseLiveData
    }


    fun getMedicationDetail(id: Int): LiveData<Event<DataResult<GetMedicationDetailResponse>>> {
        viewModelScope.launch {
            val response = medListRepository.getMedicationDetail(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getMedicationDetailResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getMedicationDetailResponseLiveData
    }

    // add new medicine for medlist
    fun addNewMedlistMedicine(
        medicineMedlist: AddMedListRequestModel
    ): LiveData<Event<DataResult<AddedMedlistResponseModel>>> {
        viewModelScope.launch {
            val response =
                medicineMedlist.let { medListRepository.addNewMedlistMedicine(it) }
            withContext(Dispatchers.Main) {
                response.collect {
                    _addMedicineResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addMedicineResponseLiveData
    }


}
