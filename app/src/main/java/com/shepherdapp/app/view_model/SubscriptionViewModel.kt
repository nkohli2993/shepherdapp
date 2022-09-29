package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.subscription.SubscriptionModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Deepak Rattan
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {


    // Buy Subscription Plan Live Data
    private val _openSubscriptionPlanLiveData = MutableLiveData<SingleEvent<SubscriptionModel>>()
    val openSubscriptionPlanLiveData: LiveData<SingleEvent<SubscriptionModel>> get() = _openSubscriptionPlanLiveData

    fun openSubscriptionPlan(subscriptionModel: SubscriptionModel) {
        _openSubscriptionPlanLiveData.value = SingleEvent(subscriptionModel)
    }


}
