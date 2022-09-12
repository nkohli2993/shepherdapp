package com.shepherdapp.app.data.remote.relation_repository

import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherdapp.app.data.dto.edit_loved_one.EditLovedOneResponseModel
import com.shepherdapp.app.data.dto.relation.RelationResponseModel
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
 * Created by Deepak Rattan on 06/06/22
 */
@Singleton
class RelationRepository @Inject constructor(private val apiService: ApiService) {
    // Get Relations
    suspend fun getRelations(pageNumber: Int, limit: Int): Flow<DataResult<RelationResponseModel>> {
        return object : NetworkOnlineDataRepo<RelationResponseModel, RelationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<RelationResponseModel> {
                return apiService.getRelations(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Create Loved One
    suspend fun createLovedOne(createLovedOneModel: CreateLovedOneModel): Flow<DataResult<CreateLovedOneResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CreateLovedOneResponseModel, CreateLovedOneResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CreateLovedOneResponseModel> {
                return apiService.createLovedOne(createLovedOneModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    // Create Loved One
    suspend fun editLovedOne(
        uuid: String,
        createLovedOneModel: CreateLovedOneModel
    ): Flow<DataResult<EditLovedOneResponseModel>> {
        return object :
            NetworkOnlineDataRepo<EditLovedOneResponseModel, EditLovedOneResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<EditLovedOneResponseModel> {
                return apiService.editLovedOne(uuid, createLovedOneModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}