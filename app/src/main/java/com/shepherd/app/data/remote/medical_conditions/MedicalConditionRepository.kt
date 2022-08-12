package com.shepherd.app.data.remote.medical_conditions

import com.shepherd.app.data.dto.medical_conditions.MedicalConditionResponseModel
import com.shepherd.app.data.dto.medical_conditions.MedicalConditionsLovedOneRequestModel
import com.shepherd.app.data.dto.medical_conditions.UserConditionsResponseModel
import com.shepherd.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
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

    // Get Loved One's Medical Conditions
    suspend fun getLovedOneMedicalConditions(lovedOneUUID: String): Flow<DataResult<GetLovedOneMedicalConditionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetLovedOneMedicalConditionsResponseModel, GetLovedOneMedicalConditionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetLovedOneMedicalConditionsResponseModel> {
                return apiService.getLovedOneMedicalConditions(lovedOneUUID)
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

    // edit Medical Conditions for loved one id based
    suspend fun editMedicalConditions(conditions: ArrayList<MedicalConditionsLovedOneRequestModel>): Flow<DataResult<UserConditionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UserConditionsResponseModel, UserConditionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UserConditionsResponseModel> {
                return apiService.editBulkOneConditions(conditions)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


}