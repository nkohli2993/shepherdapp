package com.app.shepherd.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.remote.care_teams.CareTeamsRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.SingleEvent
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
    private val careTeamsRepository: CareTeamsRepository
) :
    BaseViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openMemberDetailsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openMemberDetails: LiveData<SingleEvent<Int>> get() = openMemberDetailsPrivate


    fun openMemberDetails(item: Int) {
        openMemberDetailsPrivate.value = SingleEvent(item)
    }


    private var _careTeamsResponseLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var careTeamsResponseLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _careTeamsResponseLiveData


    // Get Medical Conditions
    fun getCareTeams(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        viewModelScope.launch {
            val response = careTeamsRepository.getCareTeams(pageNumber, limit, status)
            withContext(Dispatchers.Main) {
                response.collect { _careTeamsResponseLiveData.postValue(Event(it)) }
            }
        }
        return careTeamsResponseLiveData
    }


}
