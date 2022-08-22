package com.shepherd.app.data.remote.vital_stats

import com.shepherd.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherd.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
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
class VitalStatsRepository @Inject constructor(private val apiService: ApiService) {

    // Add vital stats for loved one
    suspend fun addVitalStatsForLovedOne(
     vitalStats : VitalStatsRequestModel
    ): Flow<DataResult<AddVitalStatsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddVitalStatsResponseModel, AddVitalStatsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddVitalStatsResponseModel> {
                return apiService.addVitalStats(vitalStats)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}