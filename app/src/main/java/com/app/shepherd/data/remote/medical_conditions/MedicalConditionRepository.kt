package com.app.shepherd.data.remote.medical_conditions

import com.app.shepherd.data.dto.medical_conditions.MedicalConditionResponseModel
import com.app.shepherd.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.app.shepherd.data.dto.medical_conditions.UserConditionsResponseModel
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
 * Created by Deepak Rattan on 07/06/22
 */
@Singleton
class MedicalConditionRepository @Inject constructor(private val apiService: ApiService) {

    // Get Medical Conditions
    suspend fun getMedicalConditions(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<MedicalConditionResponseModel>> {
        return object :
            NetworkOnlineDataRepo<MedicalConditionResponseModel, MedicalConditionResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<MedicalConditionResponseModel> {
                return apiService.getMedicalConditions(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    // Create Bulk One Medical Conditions
    suspend fun createMedicalConditions(conditions: ArrayList<MedicalConditionsLovedOneRequestModel>): Flow<DataResult<UserConditionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UserConditionsResponseModel, UserConditionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UserConditionsResponseModel> {
                return apiService.createBulkOneConditions(conditions)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


}