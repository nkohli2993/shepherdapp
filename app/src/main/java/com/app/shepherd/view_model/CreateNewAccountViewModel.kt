package com.app.shepherd.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.data.dto.add_loved_one.UploadPicResponseModel
import com.app.shepherd.data.dto.login.LoginResponseModel
import com.app.shepherd.data.dto.signup.UserSignupData
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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

    var imageFile: File? = null

    private var _uploadImageLiveData = MutableLiveData<Event<DataResult<UploadPicResponseModel>>>()
    var uploadImageLiveData: LiveData<Event<DataResult<UploadPicResponseModel>>> =
        _uploadImageLiveData


    private var _signUpLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var signUpLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _signUpLiveData


    fun createAccount(
        phoneCode: String?,
        profilePicUrl: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        passwd: String?,
        phoneNumber: String?
    ): LiveData<Event<DataResult<LoginResponseModel>>> {
        //Update the phone code
        signUpData.value.let {
            it?.firstname = firstName
            it?.lastname = lastName
            it?.email = email
            it?.password = passwd
            it?.phoneCode = phoneCode
            it?.phoneNo = phoneNumber
            it?.profilePhoto = profilePicUrl
            it?.roleId = "1"
        }
        viewModelScope.launch {
            val response = signUpData.value?.let { authRepository.signup(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _signUpLiveData.postValue(Event(it))
                }
            }
        }
        return signUpLiveData
    }

    //Upload Image
    fun uploadImage(file: File?): LiveData<Event<DataResult<UploadPicResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.uploadImage(file)
            withContext(Dispatchers.Main) {
                response.collect {
                    _uploadImageLiveData.postValue(Event(it))
                }
            }
        }
        return uploadImageLiveData
    }

    // Save Successfully Registered User's Info into Preferences
    fun saveUser(user: UserProfiles) {
        userRepository.saveUser(user)
    }

    // Save token
    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    // Save userID
    fun saveUserId(id: Int) {
        userRepository.saveUserId(id)
    }
}