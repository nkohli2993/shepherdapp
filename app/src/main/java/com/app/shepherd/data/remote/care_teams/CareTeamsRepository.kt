package com.app.shepherd.data.remote.care_teams

import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
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
 * Created by Deepak Rattan on 08/06/22
 */
@Singleton
class CareTeamsRepository @Inject constructor(private val apiService: ApiService) {

    // Get Care Teams
    suspend fun getCareTeams(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getCareTeams(pageNumber, limit, status)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    // Get Care Teams
    suspend fun getCareTeamRoles(
        pageNumber: Int,
        limit: Int,
        status: Int
    ): Flow<DataResult<CareTeamsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<CareTeamsResponseModel, CareTeamsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<CareTeamsResponseModel> {
                return apiService.getCareTeamRoles(pageNumber, limit, status)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}