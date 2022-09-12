package com.shepherdapp.app.data.remote.security_code
import com.shepherdapp.app.data.dto.security_code.SecurityCodeResponseModel
import com.shepherdapp.app.data.dto.security_code.SendSecurityCodeRequestModel
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
    ): Flow<DataResult<SecurityCodeResponseModel>> {
        return object :
            NetworkOnlineDataRepo<SecurityCodeResponseModel, SecurityCodeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<SecurityCodeResponseModel> {
                return apiService.resetSecurityCode(response)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}