package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.user.Payload
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
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
 * Created by Deepak Rattan on 06/06/22
 */
@HiltViewModel
class WelcomeUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    /* private var _loggedInUserLiveData = MutableLiveData<Event<UserProfile?>>()
     var loggedInUserLiveData: LiveData<Event<UserProfile?>> = _loggedInUserLiveData*/

    private var _userDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailsResponseModel>>>()
    var userDetailsLiveData: LiveData<Event<DataResult<UserDetailsResponseModel>>> =
        _userDetailsLiveData


    // Get LoggedIn User Detail from SharedPrefs
    /* fun getUser(): LiveData<Event<UserProfile?>> {
         val user = userRepository.getCurrentUser()
         _loggedInUserLiveData.postValue(Event(user))
         return loggedInUserLiveData

     }*/

    //get userID from Shared Pref
    private fun getUserId(): Int? {
        return userRepository.getUserId()
    }

    //get UUID from Shared Pref
    private fun getUUID(): String? {
        return userRepository.getUUID()
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


    // Save User to SharePrefs
    fun saveUser(user: UserProfiles?) {
        userRepository.saveUser(user)
    }

    //Save Payload
    fun savePayload(payload: Payload?) {
        userRepository.savePayload(payload)
    }
}