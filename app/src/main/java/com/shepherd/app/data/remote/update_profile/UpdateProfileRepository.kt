package com.shepherd.app.data.remote.update_profile

import android.webkit.MimeTypeMap
import com.shepherd.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherd.app.data.dto.edit_profile.UserUpdateData
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
    suspend fun updateProfile(value: UserUpdateData,id:Int): Flow<DataResult<LoginResponseModel>> {
        return object : NetworkOnlineDataRepo<LoginResponseModel, LoginResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LoginResponseModel> {
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