package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions.GetPreviousSubscriptionsResponseModel
import com.shepherdapp.app.data.dto.subscription.get_active_subscriptions.GetActiveSubscriptionResponseModel
import com.shepherdapp.app.data.remote.subscription.SubscriptionRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan
 */
@HiltViewModel
class MySubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
) : BaseViewModel() {


    private var _getActiveSubscriptionResponseLiveData =
        MutableLiveData<Event<DataResult<GetActiveSubscriptionResponseModel>>>()
    var getActiveSubscriptionResponseLiveData: LiveData<Event<DataResult<GetActiveSubscriptionResponseModel>>> =
        _getActiveSubscriptionResponseLiveData

    private var _getPreviousSubscriptionResponseLiveData =
        MutableLiveData<Event<DataResult<GetPreviousSubscriptionsResponseModel>>>()
    var getPreviousSubscriptionResponseLiveData: LiveData<Event<DataResult<GetPreviousSubscriptionsResponseModel>>> =
        _getPreviousSubscriptionResponseLiveData

    fun getActiveSubscriptions(): LiveData<Event<DataResult<GetActiveSubscriptionResponseModel>>> {
        viewModelScope.launch {
            val response = subscriptionRepository.getActiveSubscription()
            withContext(Dispatchers.Main) {
                response.collect {
                    _getActiveSubscriptionResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getActiveSubscriptionResponseLiveData
    }

    fun getPreviousSubscriptions(
        page: Int,
        limit: Int
    ): LiveData<Event<DataResult<GetPreviousSubscriptionsResponseModel>>> {
        viewModelScope.launch {
            val response = subscriptionRepository.getPreviousSubscriptions(page, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getPreviousSubscriptionResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getPreviousSubscriptionResponseLiveData
    }

}




