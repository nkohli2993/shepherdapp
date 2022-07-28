package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherd.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherd.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherd.app.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.shepherd.app.data.dto.lock_box.upload_multiple_lock_box_doc.UploadMultipleLockBoxDoxResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.lock_box.LockBoxRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Deepak Rattan
 */
@HiltViewModel
class AddNewLockBoxViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var imageFile: File? = null

    private var _uploadLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadLockBoxDocResponseModel>>>()
    var uploadLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadLockBoxDocResponseModel>>> =
        _uploadLockBoxDocResponseLiveData

    private var _uploadMultipleLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadMultipleLockBoxDoxResponseModel>>>()
    var uploadMultipleLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadMultipleLockBoxDoxResponseModel>>> =
        _uploadMultipleLockBoxDocResponseLiveData

    private var _addNewLockBoxResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewLockBoxResponseModel>>>()
    var addNewLockBoxResponseLiveData: LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> =
        _addNewLockBoxResponseLiveData

    private var _getUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>>()
    var getUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> =
        _getUploadedLockBoxDocResponseLiveData

    private var _deleteUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>>()
    var deleteUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> =
        _deleteUploadedLockBoxDocResponseLiveData

    // Upload lock box document
    fun uploadLockBoxDoc(file: File?): LiveData<Event<DataResult<UploadLockBoxDocResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.uploadLockBoxDoc(file)
            withContext(Dispatchers.Main) {
                response.collect {
                    _uploadLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return uploadLockBoxDocResponseLiveData
    }


    // Upload multiple lock box document
    fun uploadMultipleLockBoxDoc(files: ArrayList<File>): LiveData<Event<DataResult<UploadMultipleLockBoxDoxResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.uploadMultipleLockBoxDoc(files)
            withContext(Dispatchers.Main) {
                response.collect {
                    _uploadMultipleLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return uploadMultipleLockBoxDocResponseLiveData
    }

    // Create Lock Box
    fun addNewLockBox(
        fileName: String?,
        fileNote: String?,
        documentUrl: String?,
        lbtId: Int?
    ): LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        val addNewLockBoxRequestModel =
            AddNewLockBoxRequestModel(fileName, fileNote, lbtId, lovedOneUUId, documentUrl)

        viewModelScope.launch {
            val response = lockBoxRepository.addNewLockBox(addNewLockBoxRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addNewLockBoxResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addNewLockBoxResponseLiveData
    }

    // Get All Uploaded Lock Box Documents by Loved One UUID
    fun getAllLockBoxUploadedDocumentsByLovedOneUUID(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUId?.let {
                lockBoxRepository.getAllUploadedDocumentsByLovedOneUUID(
                    pageNumber, limit,
                    it
                )
            }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _getUploadedLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getUploadedLockBoxDocResponseLiveData
    }

    // Delete Uploaded LockBox Doc
    fun deleteUploadedLockBoxDoc(id: Int): LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.deleteUploadedLockBoxDoc(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _deleteUploadedLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return deleteUploadedLockBoxDocResponseLiveData
    }

}
