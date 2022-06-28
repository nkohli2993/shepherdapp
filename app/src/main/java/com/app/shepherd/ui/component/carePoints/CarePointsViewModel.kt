package com.app.shepherd.ui.component.carePoints

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginRequestModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.error.CHECK_YOUR_FIELDS
import com.app.shepherd.data.error.EMAIL_ERROR
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.RegexUtils.isValidEmail
import com.app.shepherd.utils.RegexUtils.isValidPassword
import com.app.shepherd.utils.RegexUtils.passwordValidated
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class CarePointsViewModel @Inject constructor(private val dataRepository: DataRepository) :
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val openChatItemsPrivate = MutableLiveData<SingleEvent<Int>>()
    val openChatItems: LiveData<SingleEvent<Int>> get() = openChatItemsPrivate

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

    fun openEventChat(item: Int) {
        openChatItemsPrivate.value = SingleEvent(item)
    }
}
