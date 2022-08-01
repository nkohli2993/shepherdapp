package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.care_team.CareTeam
import com.shepherd.app.data.dto.care_team.CareTeamModel
import com.shepherd.app.data.dto.care_team.CareTeamsResponseModel
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

    fun saveLoggedInUserTeamLead(careTeamLeaderUUID: String) {
        if (userRepository.getUUID() == careTeamLeaderUUID) {
            userRepository.saveLoggedInUserTeamLead(true)
        } else {
            userRepository.saveLoggedInUserTeamLead(false)
        }
    }

}
