package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.care_team.*
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
class MemberDetailsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var _deleteCareTeamMemberLiveData =
        MutableLiveData<Event<DataResult<DeleteCareTeamMemberResponseModel>>>()
    val deleteCareTeamMemberLiveData: LiveData<Event<DataResult<DeleteCareTeamMemberResponseModel>>> =
        _deleteCareTeamMemberLiveData

    private var _updateCareTeamMemberLiveData =
        MutableLiveData<Event<DataResult<UpdateCareTeamMemberResponseModel>>>()
    val updateCareTeamMemberLiveData: LiveData<Event<DataResult<UpdateCareTeamMemberResponseModel>>> =
        _updateCareTeamMemberLiveData

    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareMemberDetailResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareMemberDetailResponseModel>>> =
        _careTeamsResponseLiveData

    fun deleteCareTeamMember(id: Int): LiveData<Event<DataResult<DeleteCareTeamMemberResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.deleteCareTeamMember(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _deleteCareTeamMemberLiveData.postValue(Event(it))
                }
            }
        }
        return deleteCareTeamMemberLiveData
    }

    fun updateCareTeamMember(
        id: Int,
        updateCareTeamMemberRequestModel: UpdateCareTeamMemberRequestModel
    ): LiveData<Event<DataResult<UpdateCareTeamMemberResponseModel>>> {
        viewModelScope.launch {
            val response =
                careTeamsRepository.updateCareTeamMember(id, updateCareTeamMemberRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _updateCareTeamMemberLiveData.postValue(Event(it))
                }
            }
        }

        return updateCareTeamMemberLiveData
    }

    fun getCareTeamsDetail(
        id: String
    ): LiveData<Event<DataResult<CareMemberDetailResponseModel>>> {
        //val lovedOneId = userRepository.getLovedOneId()
        viewModelScope.launch {
            val response =
                careTeamsRepository.getCareTeamsDetail(
                    id
                )
            withContext(Dispatchers.Main) {
                response?.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }


    fun getLoggedInUserUUID(): String? {
        return userRepository.getUUID()
    }
}
