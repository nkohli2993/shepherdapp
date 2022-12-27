package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.medical_conditions.*
import com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions.EditMedicalConditionsResponseModel
import com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherdapp.app.data.remote.medical_conditions.MedicalConditionRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
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

    private var _addedConditionsResponseLiveData =
        MutableLiveData<Event<DataResult<AddedUserMedicalConditionResposneModel>>>()
    var addedConditionsResponseLiveData: LiveData<Event<DataResult<AddedUserMedicalConditionResposneModel>>> =
        _addedConditionsResponseLiveData

    private var _editConditionResponseLiveData =
        MutableLiveData<Event<DataResult<EditMedicalConditionsResponseModel>>>()
    var editConditionResponseLiveData: LiveData<Event<DataResult<EditMedicalConditionsResponseModel>>> =
        _editConditionResponseLiveData

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

    // add Medical Conditions
    fun addMedicalConditions(conditions: AddMedicalConditionRequestModel): LiveData<Event<DataResult<AddedUserMedicalConditionResposneModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.addMedicalConditions(conditions)
            withContext(Dispatchers.Main) {
                response.collect { _addedConditionsResponseLiveData.postValue(Event(it)) }
            }
        }
        return addedConditionsResponseLiveData
    }

    // Edit Medical Condition
    fun editMedicalCondition(
        conditions: AddMedicalConditionRequestModel,
        id: Int
    ): LiveData<Event<DataResult<EditMedicalConditionsResponseModel>>> {
        viewModelScope.launch {
            val response = medicalConditionRepository.editMedicalConditions(conditions, id)
            withContext(Dispatchers.Main) {
                response.collect { _editConditionResponseLiveData.postValue(Event(it)) }
            }
        }
        return editConditionResponseLiveData
    }


}