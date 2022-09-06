package com.shepherd.app.data.remote.med_list

import com.shepherd.app.data.dto.med_list.*
import com.shepherd.app.data.dto.med_list.get_medication_detail.GetMedicationDetailResponse
import com.shepherd.app.data.dto.med_list.loved_one_med_list.GetLovedOneMedList
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordRequestModel
import com.shepherd.app.data.dto.med_list.medication_record.MedicationRecordResponseModel
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
                return apiService.searchMedList(pageNumber, limit, search)
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

    // Get All DoseList
    suspend fun getAllDoseTypeList(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<GetAllDoseListResponseModel>> {
        return object :
            NetworkOnlineDataRepo<GetAllDoseListResponseModel, GetAllDoseListResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetAllDoseListResponseModel> {
                return apiService.getAllDoseType(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get MedLists of LovedOne
    suspend fun getLovedOneMedLists(
        lovedOneUUID: String, date: String = ""
    ): Flow<DataResult<GetLovedOneMedList>> {
        return object :
            NetworkOnlineDataRepo<GetLovedOneMedList, GetLovedOneMedList>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetLovedOneMedList> {
                return apiService.getLovedOneMedList(lovedOneUUID,date)
//                return apiService.getLovedOneMedList(lovedOneUUID)
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
        id: Int,
        scheduledMedication: UpdateScheduledMedList
    ): Flow<DataResult<AddScheduledMedicationResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddScheduledMedicationResponseModel, AddScheduledMedicationResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddScheduledMedicationResponseModel> {
                return apiService.updateScheduledMedication(id, scheduledMedication)
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

    // Add User's Medication Record
    suspend fun addUserMedicationRecord(medicationRecordRequestModel: MedicationRecordRequestModel): Flow<DataResult<MedicationRecordResponseModel>> {
        return object :
            NetworkOnlineDataRepo<MedicationRecordResponseModel, MedicationRecordResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<MedicationRecordResponseModel> {
                return apiService.addUserMedicationRecord(medicationRecordRequestModel)
            }

        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get Medication Detail
    suspend fun getMedicationDetail(id: Int): Flow<DataResult<GetMedicationDetailResponse>> {
        return object :
            NetworkOnlineDataRepo<GetMedicationDetailResponse, GetMedicationDetailResponse>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetMedicationDetailResponse> {
                return apiService.getMedicationDetails(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get Medication Records
    suspend fun getMedicationRecords(
        id: String,
        page: Int,
        limit: Int,
        date: String
    ): Flow<DataResult<GetMedicationRecordResponse>> {
        return object :
            NetworkOnlineDataRepo<GetMedicationRecordResponse, GetMedicationRecordResponse>() {
            override suspend fun fetchDataFromRemoteSource(): Response<GetMedicationRecordResponse> {
                return apiService.getMedicationRecords(id, page, limit, date)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

}