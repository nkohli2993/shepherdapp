package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.care_teams.CareTeamsRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository,
    private val careTeamsRepository: CareTeamsRepository
) :
    BaseViewModel() {

    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData

    private var _searchCareTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var searchCareTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _searchCareTeamsResponseLiveData

    private val _openChatMessage = MutableLiveData<SingleEvent<CareTeamModel>>()
    val openChatMessage: LiveData<SingleEvent<CareTeamModel>> get() = _openChatMessage

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponseModel>>()
    val loginLiveData: LiveData<Resource<LoginResponseModel>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response =
                lovedOneUUID?.let {
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

    fun searchCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int,
        search: String
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response =
                lovedOneUUID?.let {
                    careTeamsRepository.searchCareTeamsByLovedOneId(
                        pageNumber, limit, status,
                        it, search
                    )
                }
            withContext(Dispatchers.Main) {
                response?.collect { _searchCareTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return searchCareTeamsResponseLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun opnChat(careTeamModel: CareTeamModel) {
        _openChatMessage.value = SingleEvent(careTeamModel)
    }
}
