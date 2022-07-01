package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.DeleteCareTeamMemberResponseModel
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

}
