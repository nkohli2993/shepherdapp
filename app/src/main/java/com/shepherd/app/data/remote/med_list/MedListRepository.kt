package com.shepherd.app.data.remote.med_list

import com.shepherd.app.data.dto.med_list.AddScheduledMedicationResponseModel
import com.shepherd.app.data.dto.med_list.GetAllDoseListResponseModel
import com.shepherd.app.data.dto.med_list.GetAllMedListResponseModel
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.ScheduledMedicationRequestModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.DeleteAddedMedicationResponseModel
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

    // search All MedLists
    suspend fun searchMedList(
        pageNumber: Int,
        limit: Int,
        search: String
    ): Flow<DataResult<GetAllMedListResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetAllMedListResponseModel, GetAllMedListResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetAllMedListResponseModel> {
                return apiService.SearchMedList(pageNumber, limit, search)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get All DoseList
    suspend fun getAllDoseList(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<GetAllDoseListResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetAllDoseListResponseModel, GetAllDoseListResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetAllDoseListResponseModel> {
                return apiService.getAllDose(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get MedLists of LovedOne
    suspend fun getLovedOneMedLists(
        lovedOneUUID: String
    ): Flow<DataResult<GetLovedOneMedList>> {
        return object :
            NetworkOnlineDataRepo<GetLovedOneMedList, GetLovedOneMedList>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetLovedOneMedList> {
                return apiService.getLovedOneMedList(lovedOneUUID)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // add scheduled medication
    suspend fun addScheduledMedication(
        scheduledMedication: ScheduledMedicationRequestModel
    ): Flow<DataResult<AddScheduledMedicationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddScheduledMedicationResponseModel, AddScheduledMedicationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddScheduledMedicationResponseModel> {
                return apiService.addScheduledMedication(scheduledMedication)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // update scheduled medication
    suspend fun updateScheduledMedication(
        id:Int,
        scheduledMedication: ScheduledMedicationRequestModel
    ): Flow<DataResult<AddScheduledMedicationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddScheduledMedicationResponseModel, AddScheduledMedicationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddScheduledMedicationResponseModel> {
                return apiService.updateScheduledMedication(id,scheduledMedication)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // delete schedule medication
    suspend fun deletedSceduledMedication(
        id: Int
    ): Flow<DataResult<DeleteAddedMedicationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<DeleteAddedMedicationResponseModel, DeleteAddedMedicationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<DeleteAddedMedicationResponseModel> {
                return apiService.deleteAddedMedication(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}