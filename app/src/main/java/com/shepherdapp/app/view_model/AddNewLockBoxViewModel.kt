package com.shepherdapp.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.Documents
import com.shepherdapp.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherdapp.app.data.dto.lock_box.edit_lock_box.EditLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.shepherdapp.app.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.shepherdapp.app.data.dto.lock_box.upload_multiple_lock_box_doc.UploadMultipleLockBoxDoxResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
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
    private var _editNewLockBoxResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewLockBoxResponseModel>>>()
    var editNewLockBoxResponseLiveData: LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> =
        _editNewLockBoxResponseLiveData

    private var _getDetailLockBoxResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewLockBoxResponseModel>>>()
    var getDetailLockBoxResponseLiveData: LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> =
        _getDetailLockBoxResponseLiveData

    private var _getUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>>()
    var getUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> =
        _getUploadedLockBoxDocResponseLiveData

    private var _deleteUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>>()
    var deleteUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> =
        _deleteUploadedLockBoxDocResponseLiveData

    // Lock Box Type Response Live Data
    private var _lockBoxTypeResponseLiveData =
        MutableLiveData<Event<DataResult<LockBoxTypeResponseModel>>>()
    var lockBoxTypeResponseLiveData: LiveData<Event<DataResult<LockBoxTypeResponseModel>>> =
        _lockBoxTypeResponseLiveData


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
        Log.e("TAG", "uploadMultipleLockBoxDoc: ${files.size}")
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
        documentsUrl: ArrayList<String>?,
        lbtId: Int?,
        allowedUserIds: ArrayList<String>?
    ): LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        var documents: ArrayList<Documents>? = arrayListOf()
        var userIds: ArrayList<String>? = arrayListOf()
        if (documentsUrl.isNullOrEmpty()) {
            documents = null
        } else {
            for (i in documentsUrl.indices) {
                val doc = documentsUrl[i]
                documents?.add(Documents(doc))
            }
        }
        userIds = if (allowedUserIds.isNullOrEmpty()) {
            null
        } else {
            allowedUserIds
        }
        val addNewLockBoxRequestModel = AddNewLockBoxRequestModel(
            fileName, fileNote, lbtId, lovedOneUUId,
            documents, userIds
        )

        viewModelScope.launch {
            val response = addNewLockBoxRequestModel?.let { lockBoxRepository.addNewLockBox(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _addNewLockBoxResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addNewLockBoxResponseLiveData
    }

    // edit Lock Box
    fun editNewLockBox(
        fileName: String?,
        fileNote: String?,
        lbtId: Int?, id: Int,
        documentsUrl: ArrayList<Documents>?,
        documentsDeletedUrl: ArrayList<Documents>?,
        allowedUserIds: ArrayList<String>?
    ): LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        var userIds: ArrayList<String>? = arrayListOf()
        userIds = if (allowedUserIds.isNullOrEmpty()) {
            null
        } else {
            allowedUserIds
        }
        val addNewLockBoxRequestModel =
            EditLockBoxRequestModel(
                fileName,
                fileNote,
                lbtId,
                documentsUrl,
                documentsDeletedUrl,
                userIds
            )

        viewModelScope.launch {
            val response = lockBoxRepository.editNewLockBox(addNewLockBoxRequestModel, id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addNewLockBoxResponseLiveData.postValue(Event(it))
                }
            }
        }
        return addNewLockBoxResponseLiveData
    }

    //get Detail of lockbox by ID
    fun getDetailLockBox(
        id: Int
    ): LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.getDetailLockBox(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getDetailLockBoxResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getDetailLockBoxResponseLiveData
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

    fun getAllLockBoxTypes(
        pageNumber: Int,
        limit: Int,
        isQuery: Boolean
    ): LiveData<Event<DataResult<LockBoxTypeResponseModel>>> {
        val lovedOneUUId = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response =
                lockBoxRepository.getALlLockBoxTypes(pageNumber, limit, lovedOneUUId!!, isQuery)
            withContext(Dispatchers.Main) {
                response.collect {
                    _lockBoxTypeResponseLiveData.postValue(Event(it))
                }
            }
        }
        return lockBoxTypeResponseLiveData
    }


}
