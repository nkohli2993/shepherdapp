package com.shepherdapp.app.data.remote.update_profile

import android.webkit.MimeTypeMap
import com.shepherdapp.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherdapp.app.data.dto.edit_profile.UserUpdateData
import com.shepherdapp.app.data.dto.forgot_password.ForgotPasswordModel
import com.shepherdapp.app.data.dto.login.EditResponseModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.roles.RolesResponseModel
import com.shepherdapp.app.network.retrofit.ApiService
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.NetworkOnlineDataRepo
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
 * Created by Nikita kohli on 18/08/2022
 */

@Singleton
class UpdateProfileRepository @Inject constructor(private val apiService: ApiService) {


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


    //Update Profile fragment
    suspend fun updateProfile(value: UserUpdateData,id:Int): Flow<DataResult<EditResponseModel>> {
        return object : NetworkOnlineDataRepo<EditResponseModel, EditResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<EditResponseModel> {
                return apiService.updateProfile(value,id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Change Password
    suspend fun forgotPassword(value: ForgotPasswordModel): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
                return apiService.forgotPassword(value)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //get login user role
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
}