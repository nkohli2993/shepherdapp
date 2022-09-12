package com.shepherdapp.app.ui.component.edit_profile

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherdapp.app.data.dto.edit_profile.UserUpdateData
import com.shepherdapp.app.data.dto.login.EditResponseModel
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.roles.RolesResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.update_profile.UpdateProfileRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
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

    private var _updateProfileLiveData = MutableLiveData<Event<DataResult<EditResponseModel>>>()
    var updateProfileLiveData: LiveData<Event<DataResult<EditResponseModel>>> =
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
        phoneNumber: String?,
        id:Int
    ): LiveData<Event<DataResult<EditResponseModel>>> {
        //Update the phone code
        updateData.value.let {
            it?.firstname = firstName
            it?.lastname = lastName
            it?.phoneCode = phoneCode
            it?.phoneNo = phoneNumber
            it?.profilePhoto = profilePicUrl
        }

        viewModelScope.launch {
            val response = updateData.value?.let { updateProfileRespository.updateProfile(it,id) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _updateProfileLiveData.postValue(Event(it))
                }
            }
        }

        return updateProfileLiveData
    }

    // Save User to SharePrefs
    fun saveUser(user: UserProfile?) {
        userRepository.saveUser(user)
    }
}
