package com.shepherd.app.data.remote.security_code
import com.shepherd.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import com.shepherd.app.ui.base.BaseResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityCodeRepository @Inject constructor(private val apiService: ApiService) {

    // Add security code for loved one
    suspend fun addSecurityCode(
     response :SendSecurityCodeRequestModel
    ): Flow<DataResult<BaseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.addSecurityCode(response)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
    // Add security code for loved one
    suspend fun resetSecurityCode(
     response :SendSecurityCodeRequestModel
    ): Flow<DataResult<BaseResponseModel>> {
        return object :
            NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.resetSecurityCode(response)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}