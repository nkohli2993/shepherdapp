package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.care_team.CareTeamModel
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.dto.dashboard.HomeResponseModel
import com.shepherdapp.app.data.dto.invitation.delete_pending_invitee.DeletePendingInviteeByIdResponseModel
import com.shepherdapp.app.data.dto.invitation.pending_invite.PendingInviteResponseModel
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.data.remote.home_repository.HomeRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.ClickType
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
    private val userRepository: UserRepository,
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository
) :
    BaseViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMemberDetailsPrivate = MutableLiveData<SingleEvent<CareTeamModel>>()
    val openMemberDetails: LiveData<SingleEvent<CareTeamModel>> get() = openMemberDetailsPrivate

    private val _deletePendingInviteLiveData = MutableLiveData<SingleEvent<CareTeamModel>>()
    val deletePendingInviteLiveData: LiveData<SingleEvent<CareTeamModel>> get() = _deletePendingInviteLiveData


    fun openMemberDetails(careTeam: CareTeamModel, clickTypeValue: Int) {
        when (clickTypeValue) {
            ClickType.View.value -> {
                openMemberDetailsPrivate.value = SingleEvent(careTeam)
            }
            ClickType.Delete.value -> {
                _deletePendingInviteLiveData.value = SingleEvent(careTeam)
            }
        }

    }


    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData

    private var _pendingInviteResponseLiveData =
        MutableLiveData<Event<DataResult<PendingInviteResponseModel>>>()
    var pendingInviteResponseLiveData: LiveData<Event<DataResult<PendingInviteResponseModel>>> =
        _pendingInviteResponseLiveData

    private var _deletePendingInviteeByIdResponseLiveData =
        MutableLiveData<Event<DataResult<DeletePendingInviteeByIdResponseModel>>>()
    var deletePendingInviteeByIdResponseLiveData: LiveData<Event<DataResult<DeletePendingInviteeByIdResponseModel>>> =
        _deletePendingInviteeByIdResponseLiveData

    private var _homeResponseLiveData =
        MutableLiveData<Event<DataResult<HomeResponseModel>>>()
    var homeResponseLiveData: LiveData<Event<DataResult<HomeResponseModel>>> =
        _homeResponseLiveData

    private var _userDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailByUUIDLiveData


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

    fun deletePendingInviteeById(id: Int): LiveData<Event<DataResult<DeletePendingInviteeByIdResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.deletePendingInviteeById(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _deletePendingInviteeByIdResponseLiveData.postValue(Event(it))
                }
            }
        }
        return deletePendingInviteeByIdResponseLiveData
    }

    fun saveLoggedInUserTeamLead(careTeamLeaderUUID: String) {
        if (userRepository.getUUID() == careTeamLeaderUUID) {
            userRepository.saveLoggedInUserTeamLead(true)
        } else {
            userRepository.saveLoggedInUserTeamLead(false)
        }
    }

    fun getHomeData(): LiveData<Event<DataResult<HomeResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        val status = 1
        // Log.d(TAG, "LovedOneID :$lovedOneId ")
        viewModelScope.launch {
            val response = lovedOneUUID?.let { homeRepository.getHomeData(it, status) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _homeResponseLiveData.postValue(Event(it))
                }
            }
        }
        return homeResponseLiveData
    }

    //get userinfo from Shared Pref
    fun getLovedUserDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }

    fun getLovedOneUUID(): String? {
        return userRepository.getLovedOneUUId()
    }

    // Get User Details
    fun getUserDetailByUUID(): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
        val uuid = getLovedOneUUID()
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

    fun isLoggedInUserCareTeamLead(): Boolean? {
        return userRepository.isLoggedInUserTeamLead()
    }
}
