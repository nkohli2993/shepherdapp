package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.change_password.ChangePasswordModel
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
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
class ChangePasswordViewModel @Inject
constructor(private val authRepository: AuthRepository) :
    BaseViewModel() {
    var changePasswordData = MutableLiveData<ChangePasswordModel>().apply {
        value = ChangePasswordModel()
    }

    private var _changePasswordResponseLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()

    var changePasswordResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _changePasswordResponseLiveData


    private var _logoutResponseLiveData = MutableLiveData<Event<DataResult<BaseResponseModel>>>()

    var logoutResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _logoutResponseLiveData

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            changePasswordData.value?.apply {
                this.oldPassword = oldPassword
                this.confirmPassword = confirmPassword
                this.newPassword = newPassword
            }
            val response = changePasswordData.value?.let { authRepository.changePassword(it) }
            withContext(Dispatchers.Main) {
                response?.collect { _changePasswordResponseLiveData.postValue(Event(it)) }
            }
        }

        return changePasswordResponseLiveData
    }

    fun logOut(): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.logout()
            withContext(Dispatchers.Main) {
                response.collect {
                    _logoutResponseLiveData.postValue(Event(it))
                }
            }
        }
        return logoutResponseLiveData
    }

}