package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.subscription.check_subscription_status.CheckSubscriptionStatusResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.data.remote.subscription.SubscriptionRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LovedOneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository
) : BaseViewModel() {

    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData


    private var _checkSubscriptionStatusResponseLiveData =
        MutableLiveData<Event<DataResult<CheckSubscriptionStatusResponseModel>>>()
    var checkSubscriptionStatusResponseLiveData: LiveData<Event<DataResult<CheckSubscriptionStatusResponseModel>>> =
        _checkSubscriptionStatusResponseLiveData

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

    // Check subscription status to verify whether the new loved one can be added or not
    fun checkSubscriptionStatus(): LiveData<Event<DataResult<CheckSubscriptionStatusResponseModel>>> {
        viewModelScope.launch {
            val response = subscriptionRepository.checkSubscriptionStatus()
            withContext(Dispatchers.Main) {
                response.collect {
                    _checkSubscriptionStatusResponseLiveData.postValue(Event(it))
                }
            }
        }
        return checkSubscriptionStatusResponseLiveData
    }


    fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        val lovedOneUUID = userRepository.getLovedOneUUId()
        val loggedInUserUUID = userRepository.getUUID()
        viewModelScope.launch {
            val response =
                loggedInUserUUID?.let {
                    careTeamsRepository.getCareTeamsByLovedOneId(
                        pageNumber, limit, status,
                        it
                    )
                }
            withContext(Dispatchers.Main) {
                response?.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }


    // Save Loved One UUID
    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)
    }

    fun saveLovedOneUserDetail(userLovedOne: UserLovedOne) {
        userRepository.saveLovedOneUserDetail(userLovedOne)
    }

    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
    }

    // Save User's Role
    fun saveUserRole(role: String) {
        userRepository.saveUserRole(role)
    }

    fun getUser(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun getUUID(): String? {
        return userRepository.getUUID()
    }

    fun saveSubscriptionPurchased(isSubscriptionPurchased: Boolean) {
        userRepository.saveSubscriptionPurchased(isSubscriptionPurchased)
    }

    fun isSubscriptionPurchased(): Boolean? {
        return userRepository.isSubscriptionPurchased()
    }

    fun isUserAttachedToEnterprise(): Boolean? {
        return userRepository.isUserAttachedToEnterprise()
    }

}