package com.shepherdapp.app.data.remote.notification

import com.shepherdapp.app.data.dto.notification.NotificationResponseModel
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: ApiService,
) {
    //get notification list based on loved one user id
    suspend fun getNotificationListBasedOnLovedOne(
        page: Int,
        limit: Int,
        love_user_id: String
    ): Flow<DataResult<NotificationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<NotificationResponseModel, NotificationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<NotificationResponseModel> {
                return apiService.getUserNotifications(page, limit, love_user_id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}