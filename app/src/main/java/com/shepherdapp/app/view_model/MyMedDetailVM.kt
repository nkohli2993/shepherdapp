package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.med_list.get_medication_detail.GetMedicationDetailResponse
import com.shepherdapp.app.data.remote.med_list.MedListRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyMedDetailVM @Inject constructor(
    private val dataRepository: DataRepository,
    private val medListRepository: MedListRepository
) :
    BaseViewModel() {

    private var _getMedicationDetailResponseLiveData =
        MutableLiveData<Event<DataResult<GetMedicationDetailResponse>>>()

    var getMedicationDetailResponseLiveData: LiveData<Event<DataResult<GetMedicationDetailResponse>>> =
        _getMedicationDetailResponseLiveData

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
}