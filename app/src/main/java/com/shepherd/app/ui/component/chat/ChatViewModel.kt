package com.shepherd.app.ui.component.chat

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.Resource
import com.shepherd.app.data.dto.login.LoginRequestModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.error.CHECK_YOUR_FIELDS
import com.shepherd.app.data.error.EMAIL_ERROR
import com.shepherd.app.ui.base.BaseViewModel
import com.shepherd.app.utils.RegexUtils.isValidEmail
import com.shepherd.app.utils.RegexUtils.isValidPassword
import com.shepherd.app.utils.RegexUtils.passwordValidated
import com.shepherd.app.utils.SingleEvent
import com.shepherd.app.utils.wrapEspressoIdlingResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Sumit Kumar
 */
@HiltViewModel
class ChatViewModel @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {







}
