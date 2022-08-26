package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionResponseModel
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.data.dto.medical_conditions.UpdateMedicalConditionRequestModel
import com.shepherd.app.data.dto.medical_conditions.UserConditionsResponseModel
import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherd.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseResponseModel
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 07/06/22
 */
@HiltViewModel
class AddLovedOneConditionViewModel @Inject constructor(
    private val medicalConditionRepository: MedicalConditionRepository
) : BaseViewModel() {


    private var _medicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<MedicalConditionResponseModel>>>()
    var medicalConditionResponseLiveData: LiveData<Event<DataResult<MedicalConditionResponseModel>>> =
        _medicalConditionResponseLiveData

    private var _lovedOneMedicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>>()
    var lovedOneMedicalConditionResponseLiveData: LiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>> =
        _lovedOneMedicalConditionResponseLiveData

    private var _userConditionsResponseLiveData =
        MutableLiveData<Event<DataResult<UserConditionsResponseModel>>>()
    var userConditionsResponseLiveData: LiveData<Event<DataResult<UserConditionsResponseModel>>> =
        _userConditionsResponseLiveData

    private var _updateConditionsResponseLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var updateConditionsResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _updateConditionsResponseLiveData

    private var _userIDLiveData = MutableLiveData<Event<DataResult<Int>>>()
    var userIDLiveData: LiveData<Event<DataResult<Int>>> = _userIDLiveData


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

    // Get Loved One's Medical Conditions
    fun getLovedOneMedicalConditions(lovedOneUUID: String): LiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.getLovedOneMedicalConditions(lovedOneUUID)
            withContext(Dispatchers.Main) {
                response.collect {
                    _lovedOneMedicalConditionResponseLiveData.postValue(Event(it))
                }
            }
        }
        return lovedOneMedicalConditionResponseLiveData
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

 // update Medical Conditions
    fun updateMedicalConditions(conditions: UpdateMedicalConditionRequestModel): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.updateMedicalConditions(conditions)
            withContext(Dispatchers.Main) {
                response.collect { _updateConditionsResponseLiveData.postValue(Event(it)) }
            }
        }
        return updateConditionsResponseLiveData
    }


}