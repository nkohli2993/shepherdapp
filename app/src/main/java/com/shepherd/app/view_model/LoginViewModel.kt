package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.dto.login.UserLovedOne
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.data.dto.signup.BioMetricData
import com.shepherd.app.data.dto.signup.UserSignupData
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
    fun saveUser(user: UserProfile?) {
        userRepository.saveUser(user)
    }

    // Save AuthToken to SharedPref
    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    fun clearToken() {
        userRepository.clearToken()
    }


    // Get LoggedIn User Detail
    /*  fun getUser(): LiveData<Event<UserProfile?>> {
          val user = userRepository.getCurrentUser()
          _loggedInUserLiveData.postValue(Event(user))
          return loggedInUserLiveData

      }
  */


    fun saveLovedOneId(id: String) {
        userRepository.saveLovedOneId(id)
    }

    fun saveEmail(id: String) {
        userRepository.saveEmail(id)
    }

    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)

    }

    // Save userID
    fun saveUserId(id: Int) {
        userRepository.saveUserId(id)
    }

    // Save UUID
    fun saveUUID(id: String) {
        userRepository.saveUUID(id)
    }

    // Save User's Role
    fun saveUserRole(role: String) {
        userRepository.saveUserRole(role)
    }

    fun saveLovedOneDetail(userLovedOne: UserLovedOne){
        userRepository.saveLovedOneUserDetail(userLovedOne)
    }
}