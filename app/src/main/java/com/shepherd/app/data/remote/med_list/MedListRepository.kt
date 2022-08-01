package com.shepherd.app.data.remote.med_list

import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Deepak Rattan on 01/08/22
 */

@Singleton
class MedListRepository @Inject constructor(private val apiService: ApiService) {

    // Get All MedLists
    suspend fun getAllMedLists(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<GetAllMedListResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetAllMedListResponseModel, GetAllMedListResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetAllMedListResponseModel> {
                return apiService.getAllMedLists(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}