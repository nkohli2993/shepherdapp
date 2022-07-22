package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.lock_box.lock_box_type.LockBoxTypeResponseModel
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
 * Created by Deepak Rattan
 */
@HiltViewModel
class LockBoxViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository
) : BaseViewModel() {

    private var _lockBoxTypeResponseLiveData =
        MutableLiveData<Event<DataResult<LockBoxTypeResponseModel>>>()
    var lockBoxTypeResponseLiveData: LiveData<Event<DataResult<LockBoxTypeResponseModel>>> =
        _lockBoxTypeResponseLiveData

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


}
