package com.shepherdapp.app.data.remote.medical_conditions

import com.shepherdapp.app.data.dto.medical_conditions.*
import com.shepherdapp.app.data.dto.medical_conditions.edit_medical_conditions.EditMedicalConditionsResponseModel
import com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
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
    suspend fun updateMedicalConditions(conditions: UpdateMedicalConditionRequestModel): Flow<DataResult<BaseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.updateMedicalConditions(conditions)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //add medical conditions
    suspend fun addMedicalConditions(conditions: AddMedicalConditionRequestModel): Flow<DataResult<AddedUserMedicalConditionResposneModel>> {
        return object :
            NetworkOnlineDataRepo<AddedUserMedicalConditionResposneModel, AddedUserMedicalConditionResposneModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddedUserMedicalConditionResposneModel> {
                return apiService.addMedicalConditions(conditions)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //Edit medical conditions
    suspend fun editMedicalConditions(
        conditions: AddMedicalConditionRequestModel,
        id: Int
    ): Flow<DataResult<EditMedicalConditionsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<EditMedicalConditionsResponseModel, EditMedicalConditionsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<EditMedicalConditionsResponseModel> {
                return apiService.editMedicalConditions(conditions, id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}