package com.app.shepherd.data.remote.lock_box

import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockBoxRepository @Inject constructor(private val apiService: ApiService) {

    // Get All Lock Box Types
    suspend fun getALlLockBoxTypes(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<LockBoxTypeResponseModel>> {
        return object :
            NetworkOnlineDataRepo<LockBoxTypeResponseModel, LockBoxTypeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LockBoxTypeResponseModel> {
                return apiService.getAllLockBoxTypes(
                    pageNumber,
                    limit,
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}