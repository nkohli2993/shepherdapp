package com.shepherdapp.app.data.remote.subscription

import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.SubscriptionResponseModel
import com.shepherdapp.app.data.dto.subscription.check_subscription_status.CheckSubscriptionStatusResponseModel
import com.shepherdapp.app.data.dto.subscription.getPreviousSubscriptions.GetPreviousSubscriptionsResponseModel
import com.shepherdapp.app.data.dto.subscription.get_active_subscriptions.GetActiveSubscriptionResponseModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionResponseModel
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Deepak Rattan on 22/11/22
 */
@Singleton
data class SubscriptionRepository @Inject constructor(private val apiService: ApiService) {

    //validate subscription
    suspend fun validateSubscription(validateSubscriptionRequestModel: ValidateSubscriptionRequestModel): Flow<DataResult<ValidateSubscriptionResponseModel>> {
        return object :
            NetworkOnlineDataRepo<ValidateSubscriptionResponseModel, ValidateSubscriptionResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ValidateSubscriptionResponseModel> {
                return apiService.validateSubscription(validateSubscriptionRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //create subscription
    suspend fun createSubscription(subscriptionRequestModel: SubscriptionRequestModel): Flow<DataResult<SubscriptionResponseModel>> {
        return object :
            NetworkOnlineDataRepo<SubscriptionResponseModel, SubscriptionResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<SubscriptionResponseModel> {
                return apiService.createSubscription(subscriptionRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //get active subscription
    suspend fun getActiveSubscription(): Flow<DataResult<GetActiveSubscriptionResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetActiveSubscriptionResponseModel, GetActiveSubscriptionResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetActiveSubscriptionResponseModel> {
                return apiService.getActiveSubscriptions()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //get previous subscriptions
    suspend fun getPreviousSubscriptions(
        page: Int,
        limit: Int
    ): Flow<DataResult<GetPreviousSubscriptionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetPreviousSubscriptionsResponseModel, GetPreviousSubscriptionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetPreviousSubscriptionsResponseModel> {
                return apiService.getPreviousSubscriptions(page, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    //check subscription status whether the loved one can be added or not
    suspend fun checkSubscriptionStatus(): Flow<DataResult<CheckSubscriptionStatusResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CheckSubscriptionStatusResponseModel, CheckSubscriptionStatusResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CheckSubscriptionStatusResponseModel> {
                return apiService.checkSubscriptionStatus()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}
