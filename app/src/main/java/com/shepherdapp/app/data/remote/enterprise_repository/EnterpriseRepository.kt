package com.shepherdapp.app.data.remote.enterprise_repository

import com.shepherdapp.app.data.dto.delete_account.DeleteAccountModel
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseRequestModel
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseResponseModel
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
 * Created by Deepak Rattan on 23/11/22
 */
@Singleton
data class EnterpriseRepository @Inject constructor(private val apiService: ApiService) {

    //attach enterprise
    suspend fun attachEnterprise(attachEnterpriseRequestModel: AttachEnterpriseRequestModel): Flow<DataResult<AttachEnterpriseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AttachEnterpriseResponseModel, AttachEnterpriseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AttachEnterpriseResponseModel> {
                return apiService.attachEnterprise(attachEnterpriseRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
    //delete Account
    suspend fun deleteAccount(id:Int,deleteAccountModel: DeleteAccountModel): Flow<DataResult<BaseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.deleteAccount(id,deleteAccountModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}
