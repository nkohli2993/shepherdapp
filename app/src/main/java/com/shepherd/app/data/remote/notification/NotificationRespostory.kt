package com.shepherd.app.data.remote.notification

import com.shepherd.app.data.dto.notification.NotificationResponseModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class NotificationRespostory @Inject constructor(private val apiService: ApiService) {

    //get notification list based on loved one user id
    suspend fun getNotificationListBasedOnLovedOne(page:Int, limit:Int, loveone_user_id: String): Flow<DataResult<NotificationResponseModel>> {
        return object : NetworkOnlineDataRepo<NotificationResponseModel, NotificationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<NotificationResponseModel> {
                return apiService.getNotificationListBasedOnLovedOne(page,limit,loveone_user_id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}