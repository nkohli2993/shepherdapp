package com.app.shepherd.data.remote

import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
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
 * Created by Deepak Rattan on 27/05/22
 */

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun login(value: UserSignupData): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.login(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}