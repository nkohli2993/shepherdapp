package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.BioMetricData
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
import com.app.shepherd.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.data.remote.care_teams.CareTeamsRepository
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
    private val userRepository: UserRepository,
    private val careTeamsRepository: CareTeamsRepository
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

    private var _userDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailByUUIDLiveData

    private var _lovedOneDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailsResponseModel>>>()
    var lovedOneDetailsLiveData: LiveData<Event<DataResult<UserDetailsResponseModel>>> =
        _lovedOneDetailsLiveData

    private var _lovedOneDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var lovedOneDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _lovedOneDetailByUUIDLiveData

    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData


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

    // Get User Details
    fun getUserDetailByUUID(): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
        val uuid = getUUID()
        viewModelScope.launch {
            val response = uuid?.let { authRepository.getUserDetailsByUUID(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _userDetailByUUIDLiveData.postValue(Event(it))
                }
            }
        }
        return userDetailByUUIDLiveData
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
    fun getLovedOneDetails(lovedOneUserId: String): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
//        val userID = getLovedOneUserId()
//        val uuid = getUUID()
        viewModelScope.launch {
            val response = lovedOneUserId.let { authRepository.getUserDetailsByUUID(it) }
            withContext(Dispatchers.Main) {
                response.collect {
                    _lovedOneDetailByUUIDLiveData.postValue(Event(it))
                }
            }
        }
        return lovedOneDetailByUUIDLiveData
    }


    // Get Care Teams for Logged In User
    fun getCareTeamsForLoggedInUser(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        viewModelScope.launch {
            val response =
                careTeamsRepository.getCareTeamsForLoggedInUser(pageNumber, limit, status)
            withContext(Dispatchers.Main) {
                response.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }

    // Save Loved One UUID
    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)
    }

}
