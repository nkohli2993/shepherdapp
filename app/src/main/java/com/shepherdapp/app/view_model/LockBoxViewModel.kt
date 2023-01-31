package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.shepherdapp.app.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.shepherdapp.app.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan
 */
@HiltViewModel
class LockBoxViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    // Lock Box Type Response Live Data
    private var _lockBoxTypeResponseLiveData =
        MutableLiveData<Event<DataResult<LockBoxTypeResponseModel>>>()
    var lockBoxTypeResponseLiveData: LiveData<Event<DataResult<LockBoxTypeResponseModel>>> =
        _lockBoxTypeResponseLiveData

    // Get Uploaded Lock Box Response Live Data
    private var _getUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>>()
    var getUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> =
        _getUploadedLockBoxDocResponseLiveData

    // Get Searched Uploaded Lock Box Response Live Data
    private var _getSearchedUploadedLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>>()
    var getSearchedUploadedLockBoxDocResponseLiveData: LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> =
        _getSearchedUploadedLockBoxDocResponseLiveData

    // Get Searched Uploaded Lock Box Response Live Data
    private var _deleteLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>>()
    var deleteLockBoxDocResponseLiveData: LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> =
        _deleteLockBoxDocResponseLiveData

    // Uploaded Lock Box Live Data
    private val _openUploadedLockBoxDocInfo = MutableLiveData<SingleEvent<LockBox>>()
    val openUploadedDocDetail: LiveData<SingleEvent<LockBox>> get() = _openUploadedLockBoxDocInfo

    //Recommended Lock Box Live Data
    private val _createRecommendedLockBoxDocLiveData = MutableLiveData<SingleEvent<LockBoxTypes>>()
    val createRecommendedLockBoxDocLiveData: LiveData<SingleEvent<LockBoxTypes>> =
        _createRecommendedLockBoxDocLiveData

    //Recommended Lock Box Live Data
    private val _viewRecommendedLockBoxDocLiveData = MutableLiveData<SingleEvent<LockBoxTypes>>()
    val viewRecommendedLockBoxDocLiveData: LiveData<SingleEvent<LockBoxTypes>> =
        _viewRecommendedLockBoxDocLiveData

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

    // Search All Uploaded Lock Box Documents by Loved One UUID
    fun searchAllLockBoxUploadedDocumentsByLovedOneUUID(
        pageNumber: Int,
        limit: Int,
        search: String
    ): LiveData<Event<DataResult<UploadedLockBoxDocumentsResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUId?.let {
                lockBoxRepository.searchAllUploadedDocumentsByLovedOneUUID(
                    pageNumber, limit,
                    it, search
                )
            }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _getSearchedUploadedLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return getSearchedUploadedLockBoxDocResponseLiveData
    }

    // Delete upload lockBox by ID
    fun deleteAddedLockBoxDocumentBYID(
        id: Int
    ): LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> {
        //get lovedOne UUID from shared Pref
        val lovedOneUUId = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUId?.let {
                lockBoxRepository.deleteUploadedLockBoxDoc(id)
            }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _deleteLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return deleteLockBoxDocResponseLiveData
    }


    fun openLockBoxDocDetail(lockBox: LockBox) {
        _openUploadedLockBoxDocInfo.value = SingleEvent(lockBox)
    }

    fun createRecommendedLockBoxDoc(lockBoxTypes: LockBoxTypes) {
        _createRecommendedLockBoxDocLiveData.value = SingleEvent(lockBoxTypes)
    }

    fun viewRecommendedLockBoxDOc(lockBoxTypes: LockBoxTypes) {
        _viewRecommendedLockBoxDocLiveData.value = SingleEvent(lockBoxTypes)
    }

    fun getLovedOneDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }
}
