package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.security_code.SecurityCodeResponseModel
import com.shepherdapp.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.security_code.SecurityCodeRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SecurityCodeViewModel@Inject constructor(
    private val dataRepository: SecurityCodeRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _addSecurityCodeLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var addSecurityCodeLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _addSecurityCodeLiveData

    private var _changeSecurityCodeLiveData =
        MutableLiveData<Event<DataResult<SecurityCodeResponseModel>>>()
    var changeSecurityCodeLiveData: LiveData<Event<DataResult<SecurityCodeResponseModel>>> =
        _changeSecurityCodeLiveData

    //add security code
    fun addSecurityCode(
        response : SendSecurityCodeRequestModel
    ): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.addSecurityCode(response)
            withContext(Dispatchers.Main) {
                response.collect {
                    _addSecurityCodeLiveData.postValue(Event(it))
                }
            }
        }
        return addSecurityCodeLiveData
    }
    //reset security code
    fun resetSecurityCode(
        response : SendSecurityCodeRequestModel
    ): LiveData<Event<DataResult<SecurityCodeResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.resetSecurityCode(response)
            withContext(Dispatchers.Main) {
                response.collect {
                    _changeSecurityCodeLiveData.postValue(Event(it))
                }
            }
        }
        return changeSecurityCodeLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getUserDetail(): UserProfile?{
        return userRepository.getCurrentUser()
    }
    fun saveDetail(userDetail:UserProfile){
        userRepository.saveUser(userDetail)
    }
}
