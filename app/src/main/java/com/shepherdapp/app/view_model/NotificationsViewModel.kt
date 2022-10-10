package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.Resource
import com.shepherdapp.app.data.dto.notification.Data
import com.shepherdapp.app.data.dto.notification.NotificationResponseModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationRequestModel
import com.shepherdapp.app.data.dto.notification.read_notifications.ReadNotificationsResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.notification.NotificationRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Nikita kohli 09/09/2022
 */
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) :
    BaseViewModel() {

    private var _notificationResponseLiveData =
        MutableLiveData<Event<DataResult<NotificationResponseModel>>>()
    var notificationResponseLiveData: LiveData<Event<DataResult<NotificationResponseModel>>> =
        _notificationResponseLiveData

    private var _readNotificationResponseLiveData =
        MutableLiveData<Event<DataResult<ReadNotificationsResponseModel>>>()
    var readNotificationResponseLiveData: LiveData<Event<DataResult<ReadNotificationsResponseModel>>> =
        _readNotificationResponseLiveData

    private var _clearNotificationResponseLiveData =
        MutableLiveData<Event<DataResult<ReadNotificationsResponseModel>>>()
    var clearNotificationResponseLiveData: LiveData<Event<DataResult<ReadNotificationsResponseModel>>> =
        _clearNotificationResponseLiveData

    private val _selectedNotificationLiveData =
        MutableLiveData<SingleEvent<Data>>()
    val selectedNotificationLiveData: LiveData<SingleEvent<Data>> get() = _selectedNotificationLiveData


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

    // Get User Notifications
    fun getUserNotifications(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<NotificationResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = lovedOneUUID?.let {
                notificationRepository.getNotificationListBasedOnLovedOne(
                    pageNumber, limit,
                    it
                )
            }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _notificationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return notificationResponseLiveData
    }


    fun getNotifications(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<NotificationResponseModel>>> {
        viewModelScope.launch {
            val response = notificationRepository.getNotifications(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _notificationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return notificationResponseLiveData
    }

    fun readNotifications(readNotificationRequestModel: ReadNotificationRequestModel?): LiveData<Event<DataResult<ReadNotificationsResponseModel>>> {
        viewModelScope.launch {
            val response = notificationRepository.readNotifications(readNotificationRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _readNotificationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return readNotificationResponseLiveData
    }

    // Clear Notifications
    fun clearNotifications(): LiveData<Event<DataResult<ReadNotificationsResponseModel>>> {
        viewModelScope.launch {
            val response = notificationRepository.clearNotifications()
            withContext(Dispatchers.Main) {
                response.collect {
                    _clearNotificationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return clearNotificationResponseLiveData
    }

    fun readNotification(data: Data) {
        _selectedNotificationLiveData.value = SingleEvent(data)
    }

}
