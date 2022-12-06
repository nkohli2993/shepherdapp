package com.shepherdapp.app.view_model

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.VitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals.BulkCreateVitalRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.update_user_profile_last_sync.UpdateUserProfileForLastSyncRequestModel
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.update_profile.UpdateProfileRepository
import com.shepherdapp.app.data.remote.vital_stats.VitalStatsRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
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
    private val userRepository: UserRepository,
    private val updateProfileRepository: UpdateProfileRepository
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

    private var _createBulkVitalStatsLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var createBulkVitalStatsLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _createBulkVitalStatsLiveData

    private var _updateProfileForLastSyncLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var updateProfileForLastSyncLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _updateProfileForLastSyncLiveData

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

    // create Bulk Vital stats
    fun createBulkVitalStats(
        bulkCreateVitalRequestModel: BulkCreateVitalRequestModel
    ): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response =
                dataRepository.createBulkVitalStatsForLovedOne(bulkCreateVitalRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _createBulkVitalStatsLiveData.postValue(Event(it))
                }
            }
        }
        return createBulkVitalStatsLiveData
    }

    // Update profile for last sync
    fun updateProfileForLastSync(
        updateUserProfileForLastSyncRequestModel: UpdateUserProfileForLastSyncRequestModel
    ): LiveData<Event<DataResult<BaseResponseModel>>> {
        val id = userRepository.getCurrentUser()?.id
        Log.d(TAG, "updateProfileForLastSync: userId : $id")
        viewModelScope.launch {
            val response =
                id?.let {
                    updateProfileRepository.updateProfileForLastSync(
                        updateUserProfileForLastSyncRequestModel,
                        it
                    )
                }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _updateProfileForLastSyncLiveData.postValue(Event(it))
                }
            }
        }
        return updateProfileForLastSyncLiveData
    }


    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
    }

    fun getUser(): UserProfile? {
        return userRepository.getCurrentUser()
    }

}
