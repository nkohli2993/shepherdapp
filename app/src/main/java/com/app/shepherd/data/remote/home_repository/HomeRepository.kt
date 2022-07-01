package com.app.shepherd.data.remote.home_repository

import com.app.shepherd.data.dto.dashboard.HomeResponseModel
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Deepak Rattan on 29/06/22
 */

@Singleton
class HomeRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getHomeData(lovedOneUserId: Int, status: Int): Flow<DataResult<HomeResponseModel>> {
        return object : NetworkOnlineDataRepo<HomeResponseModel, HomeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<HomeResponseModel> {
                return apiService.getHomeData(lovedOneUserId, status)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}