package com.shepherd.app.data.remote.resource

import com.shepherd.app.data.dto.resource.ResponseRelationModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepository @Inject constructor(private val apiService: ApiService) {

    // get all resource list
    suspend fun getAllResourceApi(
        page: Int,
        limit: Int,
        lovedOneId:String
    ): Flow<DataResult<ResponseRelationModel>> {
        return object :
            NetworkOnlineDataRepo<ResponseRelationModel, ResponseRelationModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ResponseRelationModel> {
                return apiService.getAllResourceApi(page = page, limit = limit, id = lovedOneId)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // get trending resource list
    suspend fun getTrendingResourceApi(
        page: Int,
        limit: Int
    ): Flow<DataResult<ResponseRelationModel>> {
        return object :
            NetworkOnlineDataRepo<ResponseRelationModel, ResponseRelationModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ResponseRelationModel> {
                return apiService.getTrendingResourceApi(page = page, limit = limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}