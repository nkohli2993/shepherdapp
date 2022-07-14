package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.invitation.InvitationsResponseModel
import com.app.shepherd.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
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
 * Created by Deepak Rattan on 07/06/22
 */
@HiltViewModel
class CareTeamsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {


    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData

    private var _invitationsResponseLiveData =
        MutableLiveData<Event<DataResult<InvitationsResponseModel>>>()

    val invitationsResponseLiveData: LiveData<Event<DataResult<InvitationsResponseModel>>> =
        _invitationsResponseLiveData

    private var _acceptInvitationsResponseLiveData =
        MutableLiveData<Event<DataResult<AcceptInvitationResponseModel>>>()

    val acceptInvitationsResponseLiveData: LiveData<Event<DataResult<AcceptInvitationResponseModel>>> =
        _acceptInvitationsResponseLiveData


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


    // Get Join Care Team Invitations
    fun getJoinCareTeamInvitations(
        sendType: String,
        status: Int
    ): LiveData<Event<DataResult<InvitationsResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.getJoinCareTeamInvitations(sendType, status)
            withContext(Dispatchers.Main) {
                response.collect {
                    _invitationsResponseLiveData.postValue(Event(it))
                }
            }
        }
        return invitationsResponseLiveData
    }

    // Accept Care Team Invitations
    fun acceptCareTeamInvitations(
        id: Int,
    ): LiveData<Event<DataResult<AcceptInvitationResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.acceptInvitation(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _acceptInvitationsResponseLiveData.postValue(Event(it))
                }
            }
        }
        return acceptInvitationsResponseLiveData
    }

    // Save Loved One UUID
    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)
    }

}