package com.app.shepherd.data.remote

import android.webkit.MimeTypeMap
import com.app.shepherd.data.dto.forgot_password.ForgotPasswordModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.network.retrofit.ApiService
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.NetworkOnlineDataRepo
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
    suspend fun login(value: UserSignupData): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.login(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //Upload Image
    suspend fun uploadImage(file: File?): Flow<DataResult<LoginResponseModel>> {
        var body: MultipartBody.Part? = null
        if (file != null) {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)

            extension?.let {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            val requestFile = file
                .asRequestBody(type!!.toMediaTypeOrNull())
            body =
                MultipartBody.Part.createFormData("profile", file.name, requestFile)
        }

        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
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


    // Forgot Password
    suspend fun forgotPassword(value: ForgotPasswordModel): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.forgotPassword(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}