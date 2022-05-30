package com.app.shepherd.ui.component.resetPassword

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.login.LoginRequestModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.error.EMAIL_ERROR
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.RegexUtils.isValidEmail
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val resetPasswordLiveDataPrivate = MutableLiveData<Resource<LoginResponseModel>>()
    val resetPasswordLiveData: LiveData<Resource<LoginResponseModel>> get() = resetPasswordLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun doResetPassword(email: String) {
        val isUsernameValid = isValidEmail(email)
        if (!isUsernameValid) {
            resetPasswordLiveDataPrivate.value = Resource.DataError(EMAIL_ERROR)
        } else {
            viewModelScope.launch {
                resetPasswordLiveDataPrivate.value = Resource.Loading()
                wrapEspressoIdlingResource {
                    dataRepository.doLogin(loginRequest = LoginRequestModel(email, ""))
                        .collect {
                            resetPasswordLiveDataPrivate.value = it
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
