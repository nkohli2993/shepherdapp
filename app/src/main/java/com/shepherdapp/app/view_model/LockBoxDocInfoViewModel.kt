package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.AddNewLockBoxResponseModel
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxRequestModel
import com.shepherdapp.app.data.dto.lock_box.update_lock_box.UpdateLockBoxResponseModel
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
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
        MutableLiveData<Event<DataResult<UpdateLockBoxResponseModel>>>()
    var updateLockBoxDocResponseLiveData: LiveData<Event<DataResult<UpdateLockBoxResponseModel>>> =
        _updateLockBoxDocResponseLiveData


    private var _getDetailLockBoxResponseLiveData =
        MutableLiveData<Event<DataResult<AddNewLockBoxResponseModel>>>()
    var getDetailLockBoxResponseLiveData: LiveData<Event<DataResult<AddNewLockBoxResponseModel>>> =
        _getDetailLockBoxResponseLiveData

    // Update Lock Box Doc
    fun updateLockBoxDoc(
        id: Int?,
        updateLockBoxRequestModel: UpdateLockBoxRequestModel
    ): LiveData<Event<DataResult<UpdateLockBoxResponseModel>>> {
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
    //get Detail of lockbox by ID
    fun getDetailLockBox(
        id:Int
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
}
