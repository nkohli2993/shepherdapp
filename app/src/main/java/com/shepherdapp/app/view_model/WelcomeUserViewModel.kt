package com.shepherdapp.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.TableName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 06/06/22
 */
@HiltViewModel
class WelcomeUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    /* private var _loggedInUserLiveData = MutableLiveData<Event<UserProfile?>>()
     var loggedInUserLiveData: LiveData<Event<UserProfile?>> = _loggedInUserLiveData*/
    var usersTableName: String? = null

    private var _userDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailsLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailsLiveData

    private var _logoutResponseLiveData = MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var logoutResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _logoutResponseLiveData

    // Get LoggedIn User Detail from SharedPrefs
    /* fun getUser(): LiveData<Event<UserProfile?>> {
         val user = userRepository.getCurrentUser()
         _loggedInUserLiveData.postValue(Event(user))
         return loggedInUserLiveData

     }*/

    //get userID from Shared Pref
    private fun getUserId(): Int? {
        return userRepository.getUserId()
    }

    //get UUID from Shared Pref
    private fun getUUID(): String? {
        return userRepository.getUUID()
    }

    // Get User Details
    fun getUserDetails(): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
//        val userID = getUserId()
        val uuid = getUUID()
        viewModelScope.launch {
            val response = uuid?.let { authRepository.getUserDetailsByUUID(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _userDetailsLiveData.postValue(Event(it))
                }
            }
        }
        return userDetailsLiveData
    }


    // Save User to SharePrefs
    fun saveUser(user: UserProfile?) {
        userRepository.saveUser(user)
    }

    //Save Payload
    fun savePayload(payload: Payload?) {
        userRepository.savePayload(payload)
    }

    //Save UserRole
    fun saveUserRole(role: String) {
        userRepository.saveUserRole(role)
    }

    private var _verificationResponseLiveData =
        MutableLiveData<Event<DataResult<BaseResponseModel>>>()
    var verificationResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _verificationResponseLiveData

    //Send user verification email
    fun sendUserVerificationEmail(): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.sendUserVerificationEmail()
            withContext(Dispatchers.Main) {
                response.collect {
                    _verificationResponseLiveData.postValue(Event(it))
                }
            }
        }
        return verificationResponseLiveData
    }

    fun logOut(): LiveData<Event<DataResult<BaseResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.logout()
            withContext(Dispatchers.Main) {
                response.collect {
                    _logoutResponseLiveData.postValue(Event(it))
                }
            }
        }
        return logoutResponseLiveData
    }

    // Clear Firebase Token on logout
    fun clearFirebaseToken() {
        usersTableName = if (BuildConfig.BASE_URL == "https://sheperdstagging.itechnolabs.tech/") {
            TableName.USERS_DEV
        } else {
            TableName.USERS
        }
        val uuid = userRepository.getUUID()
        Log.d(TAG, "uuid : $uuid")
        ShepherdApp.db.collection(usersTableName!!).whereEqualTo("uuid", userRepository.getUUID())
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {
                    val documentID = it.documents[0].id
                    // Clear firebaseToken
                    ShepherdApp.db.collection(usersTableName!!).document(documentID)
                        .update("firebase_token", "")
                }
            }
    }
}