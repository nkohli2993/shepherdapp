package com.shepherd.app.ui.component.edit_profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.DataRepository
import com.shepherd.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherd.app.data.dto.added_events.*
import com.shepherd.app.data.dto.dashboard.LoveUser
import com.shepherd.app.data.dto.edit_profile.UserUpdateData
import com.shepherd.app.data.dto.login.LoginResponseModel
import com.shepherd.app.data.dto.login.UserProfile
import com.shepherd.app.data.dto.roles.RolesResponseModel
import com.shepherd.app.data.dto.signup.UserSignupData
import com.shepherd.app.data.local.UserRepository
import com.shepherd.app.data.remote.care_point.CarePointRepository
import com.shepherd.app.data.remote.update_profile.UpdateProfileRepository
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
import java.io.File
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val updateProfileRespository: UpdateProfileRepository,
    private val userRepository: UserRepository,
) :
    BaseViewModel() {
    var imageFile: File? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    private var _updateProfileLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var updateProfileLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _updateProfileLiveData

    //get userinfo from Shared Pref
    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun getUserEmail() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.EMAIL_ID, "")

    var updateData = MutableLiveData<UserUpdateData>().apply {
        value = UserUpdateData()
    }


    private var _rolesResponseLiveData = MutableLiveData<Event<DataResult<RolesResponseModel>>>()
    var rolesResponseLiveData: LiveData<Event<DataResult<RolesResponseModel>>> =
        _rolesResponseLiveData

    private var _uploadImageLiveData = MutableLiveData<Event<DataResult<UploadPicResponseModel>>>()
    var uploadImageLiveData: LiveData<Event<DataResult<UploadPicResponseModel>>> =
        _uploadImageLiveData

    //Upload Image
    fun uploadImage(file: File?): LiveData<Event<DataResult<UploadPicResponseModel>>> {
        viewModelScope.launch {
            val response = updateProfileRespository.uploadImage(file)
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
            val response = updateProfileRespository.getRoles(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _rolesResponseLiveData.postValue(Event(it))
                }
            }
        }
        return rolesResponseLiveData
    }

    fun updateAccount(
        phoneCode: String?,
        profilePicUrl: String?,
        firstName: String?,
        lastName: String?,
        email: String?,
        phoneNumber: String?,
        roleId: String?
    ): LiveData<Event<DataResult<LoginResponseModel>>> {
        //Update the phone code
        updateData.value.let {
            it?.firstname = firstName
            it?.lastname = lastName
            it?.email = email
            it?.phoneCode = phoneCode
            it?.phoneNo = phoneNumber
            it?.profilePhoto = profilePicUrl
            it?.roleId = roleId
        }

        viewModelScope.launch {
            val response = updateData.value?.let { updateProfileRespository.updateProfile(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _updateProfileLiveData.postValue(Event(it))
                }
            }
        }

        return updateProfileLiveData
    }

}
