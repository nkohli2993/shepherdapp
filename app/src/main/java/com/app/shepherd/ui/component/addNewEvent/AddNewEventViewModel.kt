package com.app.shepherd.ui.component.addNewEvent

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.Resource
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneModel
import com.app.shepherd.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.app.shepherd.data.dto.care_team.CareTeamsResponseModel
import com.app.shepherd.data.dto.login.LoginRequestModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.error.CHECK_YOUR_FIELDS
import com.app.shepherd.data.error.EMAIL_ERROR
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.RegexUtils.isValidEmail
import com.app.shepherd.utils.RegexUtils.isValidPassword
import com.app.shepherd.utils.RegexUtils.passwordValidated
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class AddNewEventViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val userRepository: UserRepository
) :
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

    private var _eventMemberLiveData =
        MutableLiveData<Event<DataResult<CareTeamsResponseModel>>>()
    var eventMemberLiveData: LiveData<Event<DataResult<CareTeamsResponseModel>>> =
        _eventMemberLiveData

    private var _createEventLiveData =
        MutableLiveData<Event<DataResult<CreateEventResponseModel>>>()
    var createEventLiveData: LiveData<Event<DataResult<CreateEventResponseModel>>> =
        _createEventLiveData

    var createEventData = MutableLiveData<CreateEventModel>().apply {
        value = CreateEventModel()
    }

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

    fun getMembers(
        pageNumber: Int,
        limit: Int,
        status: Int,
        lovedOneId: String?
    ): LiveData<Event<DataResult<CareTeamsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getMembers(pageNumber, limit, status, lovedOneId)
            withContext(Dispatchers.Main) {
                response?.collect {
                    _eventMemberLiveData.postValue(Event(it))
                }
            }
        }
        return eventMemberLiveData
    }


    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun createEvent(
        loved_one_user_id: String?,
        name: String,
        location: String,
        date: String,
        time: String,
        notes: String,
        assign_to: ArrayList<String>
    ): LiveData<Event<DataResult<CreateEventResponseModel>>> {
        createEventData.value.let {
            it?.loved_one_user_id = loved_one_user_id
            it?.name = name
            it?.location = location
            it?.date = date
            it?.time = time
            it?.notes = notes
            it?.assign_to = assign_to
        }


        viewModelScope.launch {
            val response = createEventData.value?.let { dataRepository.createEvent(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _createEventLiveData.postValue(Event(it))
                }
            }
        }
        return createEventLiveData
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }

}
