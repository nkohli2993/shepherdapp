package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
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
class AddMemberViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val careTeamsRepository: CareTeamsRepository
) :
    BaseViewModel() {
    private var _careTeamRolesResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamRolesResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamRolesResponseLiveData


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

}
