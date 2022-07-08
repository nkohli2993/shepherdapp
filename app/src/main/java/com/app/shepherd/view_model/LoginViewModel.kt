package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.login.UserProfile
import com.app.shepherd.data.dto.signup.BioMetricData
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
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
    var bioMetricData = MutableLiveData<BioMetricData>().apply {
        value = BioMetricData()
    }

    private var _loginResponseLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()

    var loginResponseLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _loginResponseLiveData

    private var _loggedInUserLiveData = MutableLiveData<Event<UserProfile?>>()
    var loggedInUserLiveData: LiveData<Event<UserProfile?>> = _loggedInUserLiveData

    private var _bioMetricLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var bioMetricLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _bioMetricLiveData

    fun login(isBioMetric: Boolean): LiveData<Event<DataResult<LoginResponseModel>>> {
        viewModelScope.launch {
            val response = loginData.value?.let { authRepository.login(it, isBioMetric) }
            withContext(Dispatchers.Main) {
                response?.collect { _loginResponseLiveData.postValue(Event(it)) }
            }
        }

        return loginResponseLiveData
    }

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

    // Save User to SharePrefs
    fun saveUser(user: UserProfiles?) {
        userRepository.saveUser(user)
    }

    // Save AuthToken to SharedPref
    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    fun clearToken() {
        userRepository.clearToken()
    }

    fun saveLovedOneId(lovedOneID: Int?) {
        lovedOneID?.let { userRepository.saveLovedOneId(it) }
    }

    // Get LoggedIn User Detail
    /*  fun getUser(): LiveData<Event<UserProfile?>> {
          val user = userRepository.getCurrentUser()
          _loggedInUserLiveData.postValue(Event(user))
          return loggedInUserLiveData

      }
  */

    fun saveLovedOneId(lovedOneID: Int) {
        userRepository.saveLovedOneId(lovedOneID)
    }

    // Save userID
    fun saveUserId(id: Int) {
        userRepository.saveUserId(id)
    }

    // Save UUID
    fun saveUUID(id: String) {
        userRepository.saveUUID(id)
    }
}