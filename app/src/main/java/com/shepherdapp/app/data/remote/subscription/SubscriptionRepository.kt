package com.shepherdapp.app.data.remote.subscription

import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.SubscriptionResponseModel
import com.shepherdapp.app.data.dto.subscription.check_subscription_status.CheckSubscriptionStatusResponseModel
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

    //create subscription
    suspend fun createSubscription(subscriptionRequestModel: SubscriptionRequestModel): Flow<DataResult<SubscriptionResponseModel>> {
        return object :
            NetworkOnlineDataRepo<SubscriptionResponseModel, SubscriptionResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<SubscriptionResponseModel> {
                return apiService.createSubscription(subscriptionRequestModel)
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
