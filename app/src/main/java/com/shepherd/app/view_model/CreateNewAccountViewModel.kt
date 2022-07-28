package com.shepherd.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.dto.roles.RolesResponseModel
import com.shepherd.app.data.dto.signup.BioMetricData
import com.shepherd.app.data.dto.signup.UserSignupData
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.auth_repository.AuthRepository
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.Event
import com.shepherd.app.ui.base.BaseViewModel
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
    var bioMetricData = MutableLiveData<BioMetricData>().apply {
        value = BioMetricData()
    }

    var imageFile: File? = null

    private var _uploadImageLiveData = MutableLiveData<Event<DataResult<UploadPicResponseModel>>>()
    var uploadImageLiveData: LiveData<Event<DataResult<UploadPicResponseModel>>> =
        _uploadImageLiveData


    private var _signUpLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var signUpLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _signUpLiveData

    private var _bioMetricLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var bioMetricLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _bioMetricLiveData

    private var _rolesResponseLiveData = MutableLiveData<Event<DataResult<RolesResponseModel>>>()
    var rolesResponseLiveData: LiveData<Event<DataResult<RolesResponseModel>>> =
        _rolesResponseLiveData


    fun createAccount(
        phoneCode: String?,
        profilePicUrl: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        passwd: String?,
        phoneNumber: String?,
        roleId: String?
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
            it?.roleId = roleId
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

    // Biometric Registration
    fun registerBioMetric(
        isBioMetricEnable: Boolean
    ): LiveData<Event<DataResult<LoginResponseModel>>> {
        //Update the phone code
        bioMetricData.value.let {
            it?.isBiometric = isBioMetricEnable
        }
        viewModelScope.launch {
            val response = bioMetricData.value?.let { authRepository.registerBioMetric(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _bioMetricLiveData.postValue(Event(it))
                }
            }
        }
        return bioMetricLiveData
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

    //Get Roles
    fun getRoles(
        pageNumber: Int,
        limit: Int,
    ): LiveData<Event<DataResult<RolesResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.getRoles(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _rolesResponseLiveData.postValue(Event(it))
                }
            }
        }
        return rolesResponseLiveData
    }


    // Save Successfully Registered User's Info into Preferences
    /* fun saveUser(user: UserProfiles) {
         userRepository.saveUser(user)
     }*/

    // Save token
    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    // Save userID
    fun saveUserId(id: Int) {
        userRepository.saveUserId(id)
    }

    // Save uuid
    fun saveUUID(uuid: String) {
        userRepository.saveUUID(uuid)
    }

    fun saveEmail(email: String) {
        userRepository.saveEmail(email)
    }
}