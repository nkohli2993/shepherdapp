package com.shepherd.app.data.remote.resource

import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherd.app.data.dto.resource.ParticularResourceResponseModel
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
        lovedOneId: String,
        conditions: String
    ): Flow<DataResult<ResponseRelationModel>> {
        return object :
            NetworkOnlineDataRepo<ResponseRelationModel, ResponseRelationModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ResponseRelationModel> {
                return apiService.getAllResourceApi(
                    page = page,
                    limit = limit,
                    id = lovedOneId,
                    conditions = conditions
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //get result of search result
    suspend fun getSearchResourceResultApi(
        page: Int,
        limit: Int,
        lovedOneId: String,
        conditions: String,
        search: String
    ): Flow<DataResult<ResponseRelationModel>> {
        return object :
            NetworkOnlineDataRepo<ResponseRelationModel, ResponseRelationModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ResponseRelationModel> {
                return apiService.getSearchResourceResultApi(
                    page = page,
                    limit = limit,
                    id = lovedOneId,
//                    conditions = conditions,
                    search = search
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

  //get result of particular id detail
    suspend fun getResourceDetail(
        id: Int
    ): Flow<DataResult<ParticularResourceResponseModel>> {
        return object :
            NetworkOnlineDataRepo<ParticularResourceResponseModel, ParticularResourceResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<ParticularResourceResponseModel> {
                return apiService.getResourceDetail(
                    id = id
                )
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

    // Get Loved One's Medical Conditions
    suspend fun getLovedOneMedicalConditions(lovedOneUUID: String): Flow<DataResult<GetLovedOneMedicalConditionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetLovedOneMedicalConditionsResponseModel, GetLovedOneMedicalConditionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetLovedOneMedicalConditionsResponseModel> {
                return apiService.getLovedOneMedicalConditions(lovedOneUUID)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


}