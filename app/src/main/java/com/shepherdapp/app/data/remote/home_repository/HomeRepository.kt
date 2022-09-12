package com.shepherdapp.app.data.remote.home_repository

import com.shepherdapp.app.data.dto.dashboard.HomeResponseModel
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
 * Created by Deepak Rattan on 29/06/22
 */

@Singleton
class HomeRepository @Inject constructor(val apiService: ApiService) {

    suspend fun getHomeData(lovedOneUUID: String, status: Int): Flow<DataResult<HomeResponseModel>> {
        return object : NetworkOnlineDataRepo<HomeResponseModel, HomeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<HomeResponseModel> {
                return apiService.getHomeData(lovedOneUUID/*, status*/)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}