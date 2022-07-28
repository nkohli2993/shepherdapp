package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionResponseModel
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.data.dto.medical_conditions.UserConditionsResponseModel
import com.shepherd.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MedListViewModel  @Inject constructor(
    private val medicalConditionRepository: MedicalConditionRepository
) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

     var _medicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<MedicalConditionResponseModel>>>()
    var medicalConditionResponseLiveData: LiveData<Event<DataResult<MedicalConditionResponseModel>>> =
        _medicalConditionResponseLiveData

    private var _userConditionsResponseLiveData =
        MutableLiveData<Event<DataResult<UserConditionsResponseModel>>>()
    var userConditionsResponseLiveData: LiveData<Event<DataResult<UserConditionsResponseModel>>> =
        _userConditionsResponseLiveData

    private var _userIDLiveData = MutableLiveData<Event<DataResult<Int>>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    // Get Medical Conditions
    fun getMedicalConditions(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<MedicalConditionResponseModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.getMedicalConditions(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect { _medicalConditionResponseLiveData.postValue(Event(it)) }
            }
        }
        return medicalConditionResponseLiveData
    }


    // Create Bulk One Medical Conditions
    fun createMedicalConditions(conditions: ArrayList<MedicalConditionsLovedOneRequestModel>): LiveData<Event<DataResult<UserConditionsResponseModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.createMedicalConditions(conditions)
            withContext(Dispatchers.Main) {
                response.collect { _userConditionsResponseLiveData.postValue(Event(it)) }
            }
        }
        return userConditionsResponseLiveData
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMedDetailItemsPrivate = MutableLiveData<SingleEvent<String>>()
    val openMedDetailItems: LiveData<SingleEvent<String>> get() = openMedDetailItemsPrivate

    fun openMedDetail(item: String) {
        openMedDetailItemsPrivate.value = SingleEvent(item)
    }
}