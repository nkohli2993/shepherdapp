package com.shepherdapp.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.chat.User
import com.shepherdapp.app.data.dto.login.Enterprise
import com.shepherdapp.app.data.dto.login.Payload
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.utils.serializeToMap
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
    var firebaseToken: String? = null


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

    fun isUserAttachedToEnterprise(): Boolean? {
        return userRepository.isUserAttachedToEnterprise()
    }

    fun saveUSerAttachedToEnterprise(isUserAttachedToEnterprise: Boolean) {
        userRepository.saveUserAttachedToEnterprise(isUserAttachedToEnterprise)
    }

    // Save Enterprise Detail
    fun saveEnterpriseDetail(enterprise: Enterprise) {
        userRepository.saveEnterpriseDetail(enterprise)
    }

    // Get Enterprise Detail
    fun getEnterpriseDetail(): Enterprise? {
        return userRepository.getEnterpriseDetail()
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
        usersTableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE /*"https://sheperdstagging.itechnolabs.tech/"*/) {
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


    // Check if firebase token matches with any user in firebase, then clear the firebase token
    // Multiple user can loggedIn to same device, so firebase token should be updated for latest user
    // and clear the firebase token for old users

    fun checkIfFirebaseTokenMatchesWithOtherUser(user: User) {
        val fToken = userRepository.getFirebaseToken()
        usersTableName = if (BuildConfig.BASE_URL == "https://sheperdstagging.itechnolabs.tech/") {
            TableName.USERS_DEV
        } else {
            TableName.USERS
        }
        ShepherdApp.db.collection(usersTableName!!)
            .whereEqualTo("firebase_token", fToken)
            .get()
            .addOnSuccessListener {
                if (!it.documents.isNullOrEmpty()) {
                    it.documents.forEach { it1 ->
                        val docID = it1.id
                        // Update firebaseToken
                        ShepherdApp.db.collection(usersTableName!!).document(docID)
                            .update("firebase_token", "")
                    }
                }
                checkIfUserAlreadyExists(user)
            }
    }

    // Check if user's info already saved in Firestore
    private fun checkIfUserAlreadyExists(user: User) {
        usersTableName = if (BuildConfig.BASE_URL == "https://sheperdstagging.itechnolabs.tech/") {
            TableName.USERS_DEV
        } else {
            TableName.USERS
        }
        ShepherdApp.db.collection(usersTableName!!)
            .whereEqualTo("uuid", user.uuid)
            .get()
            .addOnSuccessListener {
                if (it.documents.isNullOrEmpty()) {
//                    isUserAlreadyExists = false
                    ShepherdApp.db.collection(usersTableName!!).add(user.serializeToMap())
                        .addOnSuccessListener {
                            ShepherdApp.db.collection(usersTableName!!).document(it.id)
                                .update("document_id", it.id)
                            user.documentID = it.id
                        }.addOnFailureListener {
                            if (BuildConfig.DEBUG) {
                                it.printStackTrace()
                                Log.d(com.shepherdapp.app.view_model.TAG, it.toString())
                            }
                        }
                } else {
                    // Update the firebase token
                    val documentID = it.documents[0].id
                    Log.d(com.shepherdapp.app.view_model.TAG, "DocumentID : $documentID")
//                    val fToken = userRepository.getFirebaseToken()
                    generateFirebaseToken(documentID)
                    /*db.collection(usersTableName!!).document(documentID)
                        .update("firebase_token", firebaseToken)*/
                }
            }.addOnFailureListener {
            }
    }

    private fun generateFirebaseToken(documentID: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }
            firebaseToken = it.result
            // Get new FCM registration token
            userRepository.saveFirebaseToken(firebaseToken)
            Log.d(TAG, "Firebase token generated: ${it.result}")
            // Update firebaseToken
            ShepherdApp.db.collection(usersTableName!!).document(documentID)
                .update("firebase_token", firebaseToken)
        }
    }


    fun isSubscriptionPurchased(): Boolean? {
        return userRepository.isSubscriptionPurchased()
    }
}