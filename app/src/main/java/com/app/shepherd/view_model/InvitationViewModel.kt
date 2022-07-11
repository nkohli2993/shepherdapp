package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.invitation.InvitationsResponseModel
import com.app.shepherd.data.dto.invitation.accept_invitation.AcceptInvitationResponseModel
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

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val careTeamsRepository: CareTeamsRepository
) : BaseViewModel() {

    private var _invitationsResponseLiveData =
        MutableLiveData<Event<DataResult<InvitationsResponseModel>>>()

    val invitationsResponseLiveData: LiveData<Event<DataResult<InvitationsResponseModel>>> =
        _invitationsResponseLiveData

    private var _acceptInvitationsResponseLiveData =
        MutableLiveData<Event<DataResult<AcceptInvitationResponseModel>>>()

    val acceptInvitationsResponseLiveData: LiveData<Event<DataResult<AcceptInvitationResponseModel>>> =
        _acceptInvitationsResponseLiveData

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

}