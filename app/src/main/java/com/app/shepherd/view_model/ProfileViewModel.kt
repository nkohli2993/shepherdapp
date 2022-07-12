package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.BioMetricData
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
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
 * Created by Sumit Kumar
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    var bioMetricData = MutableLiveData<BioMetricData>().apply {
        value = BioMetricData()
    }
    private var _bioMetricLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var bioMetricLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _bioMetricLiveData

    private var _userDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailsResponseModel>>>()
    var userDetailsLiveData: LiveData<Event<DataResult<UserDetailsResponseModel>>> =
        _userDetailsLiveData

    private var _lovedOneDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailsResponseModel>>>()
    var lovedOneDetailsLiveData: LiveData<Event<DataResult<UserDetailsResponseModel>>> =
        _lovedOneDetailsLiveData


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

    // Get User Details
    fun getUserDetails(): LiveData<Event<DataResult<UserDetailsResponseModel>>> {
        val userID = getUserId()
//        val uuid = getUUID()
        viewModelScope.launch {
            val response = userID?.let { authRepository.getUserDetails(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _userDetailsLiveData.postValue(Event(it))
                }
            }
        }
        return userDetailsLiveData
    }

    //get userID from Shared Pref
    private fun getUserId(): Int {
        return userRepository.getCurrentUser()?.userId ?: 0
    }

    //get uuid from Shared Pref
    private fun getUUID(): String? {
        return userRepository.getUUID()
    }

    // Get Loved One user ID
    private fun getLovedOneUserId(): String? {
        return userRepository.getLovedOneId()
    }


    // Get User Details
    fun getLovedOneDetails(lovedOneUserId: Int): LiveData<Event<DataResult<UserDetailsResponseModel>>> {
//        val userID = getLovedOneUserId()
//        val uuid = getUUID()
        viewModelScope.launch {
            val response = lovedOneUserId?.let { authRepository.getUserDetails(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _lovedOneDetailsLiveData.postValue(Event(it))
                }
            }
        }
        return lovedOneDetailsLiveData
    }

}
