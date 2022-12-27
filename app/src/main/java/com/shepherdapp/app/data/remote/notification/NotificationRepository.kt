package com.shepherdapp.app.data.remote.notification

import com.shepherdapp.app.data.dto.notification.NotificationResponseModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationRequestModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationsResponseModel
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


    suspend fun getNotifications(
        page: Int,
        limit: Int,
        lovedOneUUID:String
    ): Flow<DataResult<NotificationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<NotificationResponseModel, NotificationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<NotificationResponseModel> {
                return apiService.getNotifications(page, limit,lovedOneUUID)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Read Notifications
    suspend fun readNotifications(readNotificationRequestModel: ReadNotificationRequestModel?): Flow<DataResult<ReadNotificationsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<ReadNotificationsResponseModel, ReadNotificationsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ReadNotificationsResponseModel> {
                return apiService.readNotifications(readNotificationRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Clear Notifications
    suspend fun clearNotifications(): Flow<DataResult<ReadNotificationsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<ReadNotificationsResponseModel, ReadNotificationsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ReadNotificationsResponseModel> {
                return apiService.clearNotifications()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}