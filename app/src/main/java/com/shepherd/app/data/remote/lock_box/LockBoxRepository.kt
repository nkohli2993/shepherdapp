package com.shepherd.app.data.remote.lock_box

import android.webkit.MimeTypeMap
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherd.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.edit_lock_box.EditLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherd.app.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.update_lock_box.UpdateLockBoxResponseModel
import com.shepherd.app.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc.UploadMultipleLockBoxDoxResponseModel
import com.shepherd.app.network.retrofit.ApiService
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.NetworkOnlineDataRepo
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
        limit: Int,
        lovedOneUUID: String,
        isQuery:Boolean
    ): Flow<DataResult<LockBoxTypeResponseModel>> {
        return object :
            NetworkOnlineDataRepo<LockBoxTypeResponseModel, LockBoxTypeResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<LockBoxTypeResponseModel> {
                return apiService.getAllLockBoxTypes(
                    pageNumber,
                    limit,lovedOneUUID
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

    // Upload Lock Box Doc
    suspend fun uploadMultipleLockBoxDoc(files: ArrayList<File>?): Flow<DataResult<UploadMultipleLockBoxDoxResponseModel>> {
        var body: MultipartBody.Part? = null
        var multipartBodyList: ArrayList<MultipartBody.Part?> = arrayListOf()
        if (!files.isNullOrEmpty()) {
            for (i in files.indices) {
                var file = files[i]
                var type: String? = null
                val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)

                extension?.let {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                }

                val requestFile = file.asRequestBody(type!!.toMediaTypeOrNull())
                body = MultipartBody.Part.createFormData("document", file.name, requestFile)
                multipartBodyList.add(body)
            }

        }
        return object :
            NetworkOnlineDataRepo<UploadMultipleLockBoxDoxResponseModel, UploadMultipleLockBoxDoxResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UploadMultipleLockBoxDoxResponseModel> {
                return apiService.uploadMultipleLockBoxDoc(multipartBodyList)
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
    // get lockbox detail info by ID
    suspend fun getDetailLockBox(id:Int): Flow<DataResult<AddNewLockBoxResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddNewLockBoxResponseModel, AddNewLockBoxResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddNewLockBoxResponseModel> {
                return apiService.getDetailLockBox(id)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
    // edit New LockBox
    suspend fun editNewLockBox(addNewLockBoxRequestModel: EditLockBoxRequestModel, id:Int): Flow<DataResult<AddNewLockBoxResponseModel>> {
        return object :
            NetworkOnlineDataRepo<AddNewLockBoxResponseModel, AddNewLockBoxResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<AddNewLockBoxResponseModel> {
                return apiService.editNewLockBox(addNewLockBoxRequestModel,id)
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

    //Search ALl Uploaded Documents by LovedOne UUID
    suspend fun searchAllUploadedDocumentsByLovedOneUUID(
        pageNumber: Int,
        limit: Int,
        lovedOneUUID: String,
        search: String
    ): Flow<DataResult<UploadedLockBoxDocumentsResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UploadedLockBoxDocumentsResponseModel, UploadedLockBoxDocumentsResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UploadedLockBoxDocumentsResponseModel> {
                return apiService.searchAllUploadedDocumentsByLovedOneUUID(
                    pageNumber,
                    limit,
                    lovedOneUUID,
                    search
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


    //Update  LockBox Doc
    suspend fun updateLockBoxDoc(
        id: Int?,
        updateLockBoxRequestModel: UpdateLockBoxRequestModel
    ): Flow<DataResult<UpdateLockBoxResponseModel>> {
        return object :
            NetworkOnlineDataRepo<UpdateLockBoxResponseModel, UpdateLockBoxResponseModel>() {
            override suspend fun fetchDataFromRemoteSource(): Response<UpdateLockBoxResponseModel> {
                return apiService.updateLockBox(id, updateLockBoxRequestModel)
            }
        }.asFlow().flowOn(Dispatchers.IO)
    }
}

