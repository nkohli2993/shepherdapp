package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.vital_stats.VitalStatsRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Nikita kohli 22/08/2022
 */
@HiltViewModel
class AddNewVitalStatsViewModel @Inject constructor(
    private val dataRepository: VitalStatsRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _addVitatStatsLiveData =
        MutableLiveData<Event<DataResult<AddVitalStatsResponseModel>>>()
    var addVitatStatsLiveData: LiveData<Event<DataResult<AddVitalStatsResponseModel>>> =
        _addVitatStatsLiveData

    fun addVitalStats(
      vitalStats: VitalStatsRequestModel
    ): LiveData<Event<DataResult<AddVitalStatsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.addVitalStatsForLovedOne(vitalStats)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addVitatStatsLiveData.postValue(Event(it))
                }
            }
        }
        return addVitatStatsLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }

}
