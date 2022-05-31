package com.app.shepherd.view_model

import androidx.lifecycle.MutableLiveData
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.AuthRepository
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 31/05/22
 */
@HiltViewModel
class CreateNewAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var signUpData = MutableLiveData<UserSignupData>().apply {
        value = UserSignupData()
    }

}