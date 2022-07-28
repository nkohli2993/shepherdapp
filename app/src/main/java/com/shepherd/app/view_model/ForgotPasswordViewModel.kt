package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.dto.forgot_password.ForgotPasswordModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.auth_repository.AuthRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 02/06/22
 */

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var forgotPasswordData = MutableLiveData<ForgotPasswordModel>().apply {
        value = ForgotPasswordModel()
    }

    private var _forgotPasswordResponseLiveData =
        MutableLiveData<Event<DataResult<LoginResponseModel>>>()

    var forgotPasswordResponseLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _forgotPasswordResponseLiveData

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            forgotPasswordData.value?.apply {
                this.email = email
                this.type = "email"
            }
            val response = forgotPasswordData.value?.let { authRepository.forgotPassword(it) }
            withContext(Dispatchers.Main) {
                response?.collect { _forgotPasswordResponseLiveData.postValue(Event(it)) }
            }
        }
    }

}