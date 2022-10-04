package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.VitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.vital_stats.VitalStatsRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Nikita kohli 23/08/2022
 */
@HiltViewModel
class VitalStatsViewModel @Inject constructor(
    private val dataRepository: VitalStatsRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _getVitatStatsLiveData =
        MutableLiveData<Event<DataResult<VitalStatsResponseModel>>>()
    var getVitatStatsLiveData: LiveData<Event<DataResult<VitalStatsResponseModel>>> =
        _getVitatStatsLiveData

    private var _addVitalStatsLiveData =
        MutableLiveData<Event<DataResult<AddVitalStatsResponseModel>>>()
    var addVitalStatsLiveData: LiveData<Event<DataResult<AddVitalStatsResponseModel>>> =
        _addVitalStatsLiveData

    //get vital stats
    fun getVitalStats(
        date: String, loveone_user_id: String, type: String
    ): LiveData<Event<DataResult<VitalStatsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getVitalStats(date, loveone_user_id, type)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getVitatStatsLiveData.postValue(Event(it))
                }
            }
        }
        return getVitatStatsLiveData
    }

    //get vital stats
    fun getGraphDataVitalStats(
        date: String, loveone_user_id: String, type: String
    ): LiveData<Event<DataResult<VitalStatsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getGraphDataVitalStats(date, loveone_user_id, type)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getVitatStatsLiveData.postValue(Event(it))
                }
            }
        }
        return getVitatStatsLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }


    // add vital stats
    fun addVitalStats(
        vitalStats: VitalStatsRequestModel
    ): LiveData<Event<DataResult<AddVitalStatsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.addVitalStatsForLovedOne(vitalStats)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addVitalStatsLiveData.postValue(Event(it))
                }
            }
        }
        return addVitalStatsLiveData
    }

    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
    }

}
