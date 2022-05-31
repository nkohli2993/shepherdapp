package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.AuthRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    var loginData = MutableLiveData<UserSignupData>().apply {
        value = UserSignupData()
    }

    private var _loginResponseLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()

    var loginResponseLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _loginResponseLiveData


    fun login(): LiveData<Event<DataResult<LoginResponseModel>>> {
        viewModelScope.launch {
            val response = loginData.value?.let { authRepository.login(it) }
            withContext(Dispatchers.Main) {
                response?.collect { _loginResponseLiveData.postValue(Event(it)) }
            }
        }

        return loginResponseLiveData
    }

    fun saveUser(user: UserSignupData) {
        userRepository.saveUser(user)
    }

}