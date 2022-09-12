package com.shepherdapp.app.ui.component.addLovedOne

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.login.LoginRequestModel
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.error.CHECK_YOUR_FIELDS
import com.shepherdapp.app.data.error.EMAIL_ERROR
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.RegexUtils.isValidEmail
import com.shepherdapp.app.utils.RegexUtils.isValidPassword
import com.shepherdapp.app.utils.RegexUtils.passwordValidated
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddLovedOneViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<LoginResponseModel>>()
    val loginLiveData: LiveData<Resource<LoginResponseModel>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun doLogin(context: Context, userName: String, passWord: String) {
        val isUsernameValid = isValidEmail(userName)
        val isPassWordValid = isValidPassword(passWord)
        if (!isUsernameValid && !isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(CHECK_YOUR_FIELDS)
        } else if (!isUsernameValid && isPassWordValid) {
            loginLiveDataPrivate.value = Resource.DataError(EMAIL_ERROR)
        } else if (passwordValidated(context, passWord)) {
            viewModelScope.launch {
                loginLiveDataPrivate.value = Resource.Loading()
                wrapEspressoIdlingResource {
                    dataRepository.doLogin(loginRequest = LoginRequestModel(userName, passWord))
                        .collect {
                            loginLiveDataPrivate.value = it
                        }
                }
            }
        }
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }
}
