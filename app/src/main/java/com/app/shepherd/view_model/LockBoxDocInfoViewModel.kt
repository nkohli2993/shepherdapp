package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.lock_box.delete_uploaded_lock_box_doc.DeleteUploadedLockBoxDocResponseModel
import com.app.shepherd.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.app.shepherd.data.remote.lock_box.LockBoxRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class LockBoxDocInfoViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository
) :
    BaseViewModel() {

    private var _updateLockBoxDocResponseLiveData =
        MutableLiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>>()
    var updateLockBoxDocResponseLiveData: LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> =
        _updateLockBoxDocResponseLiveData

    // Update Lock Box Doc
    fun updateLockBoxDoc(
        id: Int?,
        updateLockBoxRequestModel: UpdateLockBoxRequestModel
    ): LiveData<Event<DataResult<DeleteUploadedLockBoxDocResponseModel>>> {
        viewModelScope.launch {
            val response = lockBoxRepository.updateLockBoxDoc(id, updateLockBoxRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _updateLockBoxDocResponseLiveData.postValue(Event(it))
                }
            }
        }
        return updateLockBoxDocResponseLiveData
    }
}
