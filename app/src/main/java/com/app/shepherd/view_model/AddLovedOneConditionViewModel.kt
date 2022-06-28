package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.app.shepherd.data.dto.medical_conditions.UserConditionsResponseModel
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.data.remote.medical_conditions.MedicalConditionRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
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
    private val authRepository: AuthRepository,
    private val medicalConditionRepository: MedicalConditionRepository,
    private val userRepository: UserRepository

) : BaseViewModel() {


    private var _medicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<MedicalConditionResponseModel>>>()
    var medicalConditionResponseLiveData: LiveData<Event<DataResult<MedicalConditionResponseModel>>> =
        _medicalConditionResponseLiveData

    private var _userConditionsResponseLiveData =
        MutableLiveData<Event<DataResult<UserConditionsResponseModel>>>()
    var userConditionsResponseLiveData: LiveData<Event<DataResult<UserConditionsResponseModel>>> =
        _userConditionsResponseLiveData

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


}