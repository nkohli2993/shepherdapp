package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.LockBox
import com.app.shepherd.data.dto.lock_box.get_all_uploaded_documents.UploadedLockBoxDocumentsResponseModel
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypes
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.lock_box.LockBoxRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.SingleEvent
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

    // Uploaded Lock Box Live Data
    private val _openUploadedLockBoxDocInfo = MutableLiveData<SingleEvent<LockBox>>()
    val openUploadedDocDetail: LiveData<SingleEvent<LockBox>> get() = _openUploadedLockBoxDocInfo

    //Recommended Lock Box Live Data
    private val _createRecommendedLockBoxDocLiveData = MutableLiveData<SingleEvent<LockBoxTypes>>()
    val createRecommendedLockBoxDocLiveData: LiveData<SingleEvent<LockBoxTypes>> =
        _createRecommendedLockBoxDocLiveData

    fun getAllLockBoxTypes(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<LockBoxTypeResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.getALlLockBoxTypes(pageNumber, limit)
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

    fun openLockBoxDocDetail(lockBox: LockBox) {
        _openUploadedLockBoxDocInfo.value = SingleEvent(lockBox)
    }

    fun createRecommendedLockBoxDoc(lockBoxTypes: LockBoxTypes) {
        _createRecommendedLockBoxDocLiveData.value = SingleEvent(lockBoxTypes)
    }
}
