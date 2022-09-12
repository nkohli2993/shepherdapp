package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.notification.NotificationResponseModel
import com.shepherdapp.app.data.remote.notification.NotificationRespostory
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Nikita kohli 09/09/2022
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(private val dataRepository: NotificationRespostory) :
    BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val loginLiveDataPrivate = MutableLiveData<Resource<NotificationResponseModel>>()
    val loginLiveData: LiveData<Resource<NotificationResponseModel>> get() = loginLiveDataPrivate

    /** Error handling as UI **/

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate



    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }
}
