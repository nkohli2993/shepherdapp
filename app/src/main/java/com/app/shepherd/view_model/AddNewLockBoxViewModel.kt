package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxRequestModel
import com.app.shepherd.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.app.shepherd.data.dto.lock_box.upload_lock_box_doc.UploadLockBoxDocResponseModel
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.data.remote.lock_box.LockBoxRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
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

    private var _addNewLockBoxResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewLockBoxResponseModel>>>()
    var addNewLockBoxResponseLiveData: LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> =
        _addNewLockBoxResponseLiveData

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


    fun addNewLockBox(
        fileName: String?,
        fileNote: String?,
        documentUrl: String?
    ): LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        val addNewLockBoxRequestModel =
            AddNewLockBoxRequestModel(fileName, fileNote, null, lovedOneUUId, documentUrl)

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

}
