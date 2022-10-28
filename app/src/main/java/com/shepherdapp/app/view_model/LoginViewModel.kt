package com.shepherdapp.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp.Companion.db
import com.shepherdapp.app.data.dto.chat.User
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.signup.BioMetricData
import com.shepherdapp.app.data.dto.signup.UserSignupData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.utils.serializeToMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 27/05/22
 */

const val TAG = "LoginViewModel"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var usersTableName: String? = null
    var isUserAlreadyExists: Boolean = false

    var loginData = MutableLiveData<UserSignupData>().apply {
        value = UserSignupData()
    }
    var bioMetricData = MutableLiveData<BioMetricData>().apply {
        value = BioMetricData()
    }

    private var _loginResponseLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()

    var loginResponseLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _loginResponseLiveData

    private var _loggedInUserLiveData = MutableLiveData<Event<UserProfile?>>()
    var loggedInUserLiveData: LiveData<Event<UserProfile?>> = _loggedInUserLiveData

    private var _bioMetricLiveData = MutableLiveData<Event<DataResult<LoginResponseModel>>>()
    var bioMetricLiveData: LiveData<Event<DataResult<LoginResponseModel>>> =
        _bioMetricLiveData

    fun login(isBioMetric: Boolean): LiveData<Event<DataResult<LoginResponseModel>>> {
        viewModelScope.launch {
            val response = loginData.value?.let { authRepository.login(it, isBioMetric) }
            withContext(Dispatchers.Main) {
                response?.collect { _loginResponseLiveData.postValue(Event(it)) }
            }
        }

        return loginResponseLiveData
    }

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

    // Save User to SharePrefs
    fun saveUser(user: UserProfile?) {
        userRepository.saveUser(user)
    }

    // Save AuthToken to SharedPref
    fun saveToken(token: String) {
        userRepository.saveToken(token)
    }

    fun clearToken() {
        userRepository.clearToken()
    }


    // Get LoggedIn User Detail
    /*  fun getUser(): LiveData<Event<UserProfile?>> {
          val user = userRepository.getCurrentUser()
          _loggedInUserLiveData.postValue(Event(user))
          return loggedInUserLiveData

      }
  */


    fun saveLovedOneId(id: String) {
        userRepository.saveLovedOneId(id)
    }

    fun saveEmail(id: String) {
        userRepository.saveEmail(id)
    }

    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)

    }

    // Save userID
    fun saveUserId(id: Int) {
        userRepository.saveUserId(id)
    }

    // Save UUID
    fun saveUUID(id: String) {
        userRepository.saveUUID(id)
    }

    // Save User's Role
    fun saveUserRole(role: String) {
        userRepository.saveUserRole(role)
    }

    fun saveLovedOneDetail(userLovedOne: UserLovedOne) {
        userRepository.saveLovedOneUserDetail(userLovedOne)
    }

    // Save User's Info in Firestore
    fun saveUserInfoInFirestore(user: User) {
        checkIfUserAlreadyExists(user)
    }

    // Check if user's info already saved in Firestore
    private fun checkIfUserAlreadyExists(user: User) {
        usersTableName = if (BuildConfig.BASE_URL == "https://sheperdstagging.itechnolabs.tech/") {
            TableName.USERS_DEV
        } else {
            TableName.USERS
        }
        db.collection(usersTableName!!)
            .whereEqualTo("uuid", user.uuid)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNullOrEmpty()) {
//                    isUserAlreadyExists = false
                    db.collection(usersTableName!!).add(user.serializeToMap())
                        .addOnSuccessListener {
                            db.collection(usersTableName!!).document(it.id)
                                .update("document_id", it.id)
                            user.documentID = it.id
                        }.addOnFailureListener {
                            if (BuildConfig.DEBUG) {
                                it.printStackTrace()
                                Log.d(TAG, it.toString())
                            }
                        }
                } else {
                    // Update the firebase token
                    val documentID = it.documents[0].id
                    val fToken = userRepository.getFirebaseToken()

                    db.collection(usersTableName!!).document(documentID)
                        .update("firebase_token", fToken)
                }
            }.addOnFailureListener {
            }
    }

    fun saveLoggedInUserAsLovedOne(isLoggedInUserLovedOne: Boolean) {
        userRepository.saveLoggedInUserAsLovedOne(isLoggedInUserLovedOne)
    }

    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
    }
}