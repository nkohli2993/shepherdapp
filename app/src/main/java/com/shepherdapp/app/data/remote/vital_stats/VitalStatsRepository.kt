package com.shepherdapp.app.data.remote.vital_stats

import com.shepherdapp.app.data.dto.add_vital_stats.AddVitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.VitalStatsResponseModel
import com.shepherdapp.app.data.dto.add_vital_stats.add_vital_stats.VitalStatsRequestModel
import com.shepherdapp.app.data.dto.add_vital_stats.bulk_create_vitals.BulkCreateVitalRequestModel
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
import com.shepherdapp.app.ui.base.BaseResponseModel
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
        vitalStats: VitalStatsRequestModel
    ): Flow<DataResult<AddVitalStatsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddVitalStatsResponseModel, AddVitalStatsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddVitalStatsResponseModel> {
                return apiService.addVitalStats(vitalStats)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Create Bulk vital stats for loved one
    suspend fun createBulkVitalStatsForLovedOne(
        bulkCreateVitalRequestModel: BulkCreateVitalRequestModel
    ): Flow<DataResult<BaseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.createBulkVitalStats(bulkCreateVitalRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // get vital stats for loved one
    suspend fun getVitalStats(
        date: String, lovedone_user_id: String, type: String
    ): Flow<DataResult<VitalStatsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<VitalStatsResponseModel, VitalStatsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<VitalStatsResponseModel> {
                return apiService.getVitalStats(date, lovedone_user_id, type)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // get vital stats for loved one
    suspend fun getGraphDataVitalStats(
        date: String, lovedone_user_id: String, type: String
    ): Flow<DataResult<VitalStatsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<VitalStatsResponseModel, VitalStatsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<VitalStatsResponseModel> {
                return apiService.getGraphDataVitalStats(date, lovedone_user_id, type)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}