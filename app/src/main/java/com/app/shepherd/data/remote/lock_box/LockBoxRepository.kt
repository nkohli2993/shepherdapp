package com.app.shepherd.data.remote.lock_box

import android.webkit.MimeTypeMap
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.app.shepherd.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
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

@Singleton
class LockBoxRepository @Inject constructor(private val apiService: ApiService) {

    // Get All Lock Box Types
    suspend fun getALlLockBoxTypes(
        pageNumber: Int,
        limit: Int
    ): Flow<DataResult<LockBoxTypeResponseModel>> {
        return object :
            NetworkOnlineDataRepo<LockBoxTypeResponseModel, LockBoxTypeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LockBoxTypeResponseModel> {
                return apiService.getAllLockBoxTypes(
                    pageNumber,
                    limit,
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Upload Lock Box Doc
    suspend fun uploadLockBoxDoc(file: File?): Flow<DataResult<UploadLockBoxDocResponseModel>> {
        var body: MultipartBody.Part? = null
        if (file != null) {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)

            extension?.let {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            val requestFile = file.asRequestBody(type!!.toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("document", file.name, requestFile)
        }
        return object :
            NetworkOnlineDataRepo<UploadLockBoxDocResponseModel, UploadLockBoxDocResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UploadLockBoxDocResponseModel> {
                return apiService.uploadLockBoxDoc(body)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    // Add New LockBox
    suspend fun addNewLockBox(addNewLockBoxRequestModel: AddNewLockBoxRequestModel): Flow<DataResult<AddNewLockBoxResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddNewLockBoxResponseModel, AddNewLockBoxResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddNewLockBoxResponseModel> {
                return apiService.addNewLockBox(addNewLockBoxRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}

