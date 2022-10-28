package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.invitation.pending_invite.PendingInviteResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class CareTeamMembersViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMemberDetailsPrivate = MutableLiveData<SingleEvent<CareTeamModel>>()
    val openMemberDetails: LiveData<SingleEvent<CareTeamModel>> get() = openMemberDetailsPrivate


    fun openMemberDetails(careTeam: CareTeamModel) {
        openMemberDetailsPrivate.value = SingleEvent(careTeam)
    }


    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData

    private var _pendingInviteResponseLiveData =
        MutableLiveData<Event<DataResult<PendingInviteResponseModel>>>()
    var pendingInviteResponseLiveData: LiveData<Event<DataResult<PendingInviteResponseModel>>> =
        _pendingInviteResponseLiveData


    fun getCareTeamsByLovedOneId(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
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

    fun getPendingInvites(): LiveData<Event<DataResult<PendingInviteResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = careTeamsRepository.getPendingInvite(lovedOneUUID)
            withContext(Dispatchers.Main) {
                response.collect {
                    _pendingInviteResponseLiveData.postValue(Event(it))
                }
            }
        }
        return pendingInviteResponseLiveData
    }

    fun saveLoggedInUserTeamLead(careTeamLeaderUUID: String) {
        if (userRepository.getUUID() == careTeamLeaderUUID) {
            userRepository.saveLoggedInUserTeamLead(true)
        } else {
            userRepository.saveLoggedInUserTeamLead(false)
        }
    }

}
