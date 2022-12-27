package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamRequestModel
import com.shepherdapp.app.data.dto.add_new_member_care_team.AddNewMemberCareTeamResponseModel
import com.shepherdapp.app.data.dto.care_team.CareTeamsResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.care_teams.CareTeamsRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var _careTeamRolesResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamRolesResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamRolesResponseLiveData

    private var _addNewMemberCareTeamResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewMemberCareTeamResponseModel>>>()
    var addNewMemberCareTeamResponseLiveData: LiveData<Event<DataResult<AddNewMemberCareTeamResponseModel>>> =
        _addNewMemberCareTeamResponseLiveData

    // Get Care Team Roles
    fun getCareTeamRoles(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.getCareTeamRoles(pageNumber, limit, status)
            withContext(Dispatchers.Main) {
                response.collect { _careTeamRolesResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamRolesResponseLiveData
    }

    // Add New Member Care Team
    fun addNewMemberCareTeam(addNewMemberCareTeamRequestModel: AddNewMemberCareTeamRequestModel): LiveData<Event<DataResult<AddNewMemberCareTeamResponseModel>>> {
        viewModelScope.launch {
            val response =
                careTeamsRepository.addNewCareTeamMember(addNewMemberCareTeamRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addNewMemberCareTeamResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addNewMemberCareTeamResponseLiveData
    }

    fun getLovedOneUUId(): String? {
        return dataRepository.getLovedOneUUId()
    }

    fun getLoggedInUserUUID(): String? {
        return userRepository.getUUID()
    }

}
