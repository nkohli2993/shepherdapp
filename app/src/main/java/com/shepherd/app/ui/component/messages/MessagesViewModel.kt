package com.shepherd.app.ui.component.messages

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class MessagesViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {


    private val openChatMessage = MutableLiveData<SingleEvent<Any>>()
    val openChatMessageItem: LiveData<SingleEvent<Any>> get() = openChatMessage

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


    /*fun doLogin(context: Context, userName: String, passWord: String) {
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
                    dataRepository.doLogin(loginRequest = LoginRequest(userName, passWord))
                        .collect {
                            loginLiveDataPrivate.value = it
                        }
                }
            }
        }
    }*/


    fun openChat(item: Any) {
        openChatMessage.value = SingleEvent(item)
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }
}
