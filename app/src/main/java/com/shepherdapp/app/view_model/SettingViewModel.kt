package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.delete_account.DeleteAccountModel
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseRequestModel
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseResponseModel
import com.shepherdapp.app.data.dto.login.Enterprise
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.signup.BioMetricData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.enterprise_repository.EnterpriseRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val enterpriseRepository: EnterpriseRepository
) :
    BaseViewModel() {

    var bioMetricData = MutableLiveData<BioMetricData>().apply {
        value = BioMetricData()
    }

    private var _bioMetricLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var bioMetricLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _bioMetricLiveData

    private var _attachEnterpriseLiveData =
        MutableLiveData<Event<DataResult<AttachEnterpriseResponseModel>>>()
    var attachEnterpriseLiveData: LiveData<Event<DataResult<AttachEnterpriseResponseModel>>> =
        _attachEnterpriseLiveData

    private var _deleteAccountLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var deleteAccountLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _deleteAccountLiveData


    fun registerBioMetric(
        isBioMetricEnable: Boolean
    ): LiveData<Event<DataResult<LoginResponseModel>>> {
        //Update the phone code
        bioMetricData.value.let {
            it?.isBiometric = isBioMetricEnable
        }
        viewModelScope.launch {
            val response = bioMetricData.value?.let { authRepository.registerBioMetric(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _bioMetricLiveData.postValue(Event(it))
                }
            }
        }
        return bioMetricLiveData
    }

    fun attachEnterprise(attachEnterpriseRequestModel: AttachEnterpriseRequestModel): LiveData<Event<DataResult<AttachEnterpriseResponseModel>>> {
        viewModelScope.launch {
            val response = enterpriseRepository.attachEnterprise(attachEnterpriseRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _attachEnterpriseLiveData.postValue(Event(it))
                }
            }
        }
        return attachEnterpriseLiveData
    }

    fun deleteAccount(id:Int,value: DeleteAccountModel): LiveData<Event<DataResult<AttachEnterpriseResponseModel>>> {
        viewModelScope.launch {
            val response = enterpriseRepository.deleteAccount(id,value)
            withContext(Dispatchers.Main) {
                response.collect {
                    _deleteAccountLiveData.postValue(Event(it))
                }
            }
        }
        return attachEnterpriseLiveData
    }

    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun isUserAttachedToEnterprise(): Boolean? {
        return userRepository.isUserAttachedToEnterprise()
    }

    fun saveUSerAttachedToEnterprise(isUserAttachedToEnterprise: Boolean) {
        userRepository.saveUserAttachedToEnterprise(isUserAttachedToEnterprise)
    }

    // Save Enterprise Detail
    fun saveEnterpriseDetail(enterprise: Enterprise) {
        userRepository.saveEnterpriseDetail(enterprise)
    }

    // Get Enterprise Detail
    fun getEnterpriseDetail(): Enterprise? {
        return userRepository.getEnterpriseDetail()
    }

}
