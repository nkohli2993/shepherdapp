package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionResponseModel
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
    private val medicalConditionRepository: MedicalConditionRepository
) : BaseViewModel() {


    private var _medicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<MedicalConditionResponseModel>>>()
    var medicalConditionResponseLiveData: LiveData<Event<DataResult<MedicalConditionResponseModel>>> =
        _medicalConditionResponseLiveData


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
}