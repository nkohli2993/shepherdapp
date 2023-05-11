package com.shepherdapp.app.data.remote.chat_repository

import com.shepherdapp.app.data.dto.chat.CareTeamChatNotificationModel
import com.shepherdapp.app.data.dto.push_notification.FCMResponseModel
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
class ChatRepository @Inject constructor(private val apiService: ApiService) {
    // Send Push Notifications
    suspend fun sendPushNotifications(chatNotificationModel: CareTeamChatNotificationModel): Flow<DataResult<FCMResponseModel>> {
        return object : NetworkOnlineDataRepo<FCMResponseModel, FCMResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<FCMResponseModel> {
                return apiService.sendPushCareTeamNotification(chatNotificationModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}