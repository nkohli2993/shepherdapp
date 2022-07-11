package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.DeleteCareTeamMemberResponseModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberRequestModel
import com.app.shepherd.data.dto.care_team.UpdateCareTeamMemberResponseModel
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
 * Created by Sumit Kumar
 */
@HiltViewModel
class MemberDetailsViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository,
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


    fun deleteCareTeamMember(id: String): LiveData<Event<DataResult<DeleteCareTeamMemberResponseModel>>> {
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
        id: String,
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
}
