package com.shepherd.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.settings_pages.StaticPageResponseModel
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.settings.StaticPagesRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class StaticPagesViewModel @Inject constructor(
    private val dataRepository: StaticPagesRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    private var _getStaticPagesLiveData =
        MutableLiveData<Event<DataResult<StaticPageResponseModel>>>()
    var getStaticPagesLiveData: LiveData<Event<DataResult<StaticPageResponseModel>>> =
        _getStaticPagesLiveData

    //get about terms and privacy data
    fun getStaticPagesApi(
        page:Int,limit:Int
    ): LiveData<Event<DataResult<StaticPageResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getStaticPagesApi(page,limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _getStaticPagesLiveData.postValue(Event(it))
                }
            }
        }
        return getStaticPagesLiveData
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getLovedOneId(): String? {
        return userRepository.getLovedOneId()
    }

}
