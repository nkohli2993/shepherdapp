package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.shepherd.data.dto.login.UserProfile
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 06/06/22
 */
@HiltViewModel
class WelcomeUserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private var _loggedInUserLiveData = MutableLiveData<Event<UserProfile?>>()
    var loggedInUserLiveData: LiveData<Event<UserProfile?>> = _loggedInUserLiveData


    // Get LoggedIn User Detail from SharedPrefs
    fun getUser(): LiveData<Event<UserProfile?>> {
        val user = userRepository.getCurrentUser()
        _loggedInUserLiveData.postValue(Event(user))
        return loggedInUserLiveData

    }


}