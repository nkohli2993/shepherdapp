package com.app.shepherd.data.remote.lock_box

import android.webkit.MimeTypeMap
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.app.shepherd.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
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

    //Get ALl Uploaded Documents by LovedOne UUID
    suspend fun getAllUploadedDocumentsByLovedOneUUID(
        pageNumber: Int,
        limit: Int,
        lovedOneUUID: String
    ): Flow<DataResult<UploadedLockBoxDocumentsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UploadedLockBoxDocumentsResponseModel, UploadedLockBoxDocumentsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UploadedLockBoxDocumentsResponseModel> {
                return apiService.getAllUploadedDocumentsByLovedOneUUID(
                    pageNumber,
                    limit,
                    lovedOneUUID
                )
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }

    //Delete Uploaded LockBox Doc
    suspend fun deleteUploadedLockBoxDoc(id: Int): Flow<DataResult<DeleteUploadedLockBoxDocResponseModel>> {
        return object :
            NetworkOnlineDataRepo<DeleteUploadedLockBoxDocResponseModel, DeleteUploadedLockBoxDocResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<DeleteUploadedLockBoxDocResponseModel> {
                return apiService.deleteUploadedLockBoxDoc(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}

