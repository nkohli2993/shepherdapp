package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.constants.ApiConstants
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.data.dto.med_list.Medlist
import com.shepherd.app.data.dto.med_list.schedule_medlist.TimeSelectedlist
import com.shepherd.app.data.remote.med_list.MedListRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Time
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddMedicationViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val medListRepository: MedListRepository
) :
    BaseViewModel() {
    // Uploaded Lock Box Live Data
    private val _selectedMedicationDetail = MutableLiveData<SingleEvent<Int>>()
    val selectedMedicationDetail: LiveData<SingleEvent<Int>> get() = _selectedMedicationDetail

    // Uploaded Lock Box Live Data
    private val _timeSelectedlist = MutableLiveData<SingleEvent<Int>>()
    val timeSelectedlist: LiveData<SingleEvent<Int>> get() = _timeSelectedlist

    private var _getMedListResponseLiveData =
        MutableLiveData<Event<DataResult<GetAllMedListResponseModel>>>()
    var getMedListResponseLiveData: LiveData<Event<DataResult<GetAllMedListResponseModel>>> =
        _getMedListResponseLiveData

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

    fun openScheduleMedication(medlistPosition: Int) {
        _selectedMedicationDetail.value = SingleEvent(medlistPosition)
    }

    fun setSelectedTime(timeSelectedlist: Int) {
        _timeSelectedlist.value = SingleEvent(timeSelectedlist)
    }


}
