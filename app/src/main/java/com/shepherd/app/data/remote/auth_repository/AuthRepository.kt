package com.shepherd.app.data.remote.auth_repository

import android.webkit.MimeTypeMap
import com.shepherd.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherd.app.data.dto.forgot_password.ForgotPasswordModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.dto.roles.RolesResponseModel
import com.shepherd.app.data.dto.signup.BioMetricData
import com.shepherd.app.data.dto.signup.UserSignupData
import com.shepherd.app.data.dto.user.UserDetailsResponseModel
import com.shepherd.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
import com.shepherd.app.ui.base.BaseResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Deepak Rattan on 27/05/22
 */

@Singleton
class AuthRepository @Inject constructor(private val apiService: ApiService) {

    //login
    suspend fun login(
        value: UserSignupData,
        isBioMetric: Boolean
    ): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                if (isBioMetric) {
                    return apiService.loginWithDevice(value)
                }
                return apiService.login(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //Upload Image
    suspend fun uploadImage(file: File?): Flow<DataResult<UploadPicResponseModel>> {
        var body: MultipartBody.Part? = null
        if (file != null) {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)

            extension?.let {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            val requestFile = file.asRequestBody(type!!.toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("profile", file.name, requestFile)
        }

        return object : NetworkOnlineDataRepo<UploadPicResponseModel, UploadPicResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UploadPicResponseModel> {
                return apiService.uploadImage(body)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    //Sign Up
    suspend fun signup(value: UserSignupData): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.signUp(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //BioMetric
    suspend fun registerBioMetric(value: BioMetricData): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.registerBioMetric(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    // Forgot Password
    suspend fun forgotPassword(value: ForgotPasswordModel): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.forgotPassword(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get User Details
    suspend fun getUserDetails(id: Int): Flow<DataResult<UserDetailsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UserDetailsResponseModel, UserDetailsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UserDetailsResponseModel> {
                return apiService.getUserDetails(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Get User Details By UUID
    suspend fun getUserDetailsByUUID(id: String): Flow<DataResult<UserDetailByUUIDResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UserDetailByUUIDResponseModel, UserDetailByUUIDResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UserDetailByUUIDResponseModel> {
                return apiService.getUserDetailByUUID(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }


    //logout
    suspend fun logout(): Flow<DataResult<BaseResponseModel>> {
        return object : NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.logout()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    suspend fun getRoles(
        pageNumber: Int,
        limit: Int,
    ): Flow<DataResult<RolesResponseModel>> {
        return object : NetworkOnlineDataRepo<RolesResponseModel, RolesResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<RolesResponseModel> {
                return apiService.getUserRoles(pageNumber, limit)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
    suspend fun sendUserVerificationEmail(): Flow<DataResult<BaseResponseModel>> {
        return object : NetworkOnlineDataRepo<BaseResponseModel, BaseResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<BaseResponseModel> {
                return apiService.sendUserVerificationEmail()
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}