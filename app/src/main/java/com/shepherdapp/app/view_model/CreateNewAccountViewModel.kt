package com.shepherdapp.app.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherdapp.app.data.dto.chat.User
import com.shepherdapp.app.data.dto.login.Enterprise
import com.shepherdapp.app.data.dto.login.LoginResponseModel
import com.shepherdapp.app.data.dto.roles.RolesResponseModel
import com.shepherdapp.app.data.dto.signup.BioMetricData
import com.shepherdapp.app.data.dto.signup.UserSignupData
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.TableName
import com.shepherdapp.app.utils.serializeToMap
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
    var usersTableName: String? = null
    var firebaseToken: String? = null


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
        roleId: String?,
        enterpriseCode: String?
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
            it?.enterprise_code = enterpriseCode
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

    fun saveUSerAttachedToEnterprise(isUserAttachedToEnterprise: Boolean) {
        userRepository.saveUserAttachedToEnterprise(isUserAttachedToEnterprise)
    }

    fun saveSignUp(isSignup: Boolean) {
        userRepository.saveSignUp(isSignup)
    }


    // Save User's Info in Firestore
    fun saveUserInfoInFirestore(user: User) {
        checkIfUserAlreadyExists(user)
    }

    // Check if firebase token matches with any user in firebase, then clear the firebase token
    // Multiple user can loggedIn to same device, so firebase token should be updated for latest user
    // and clear the firebase token for old users

    fun checkIfFirebaseTokenMatchesWithOtherUser(user: User) {
        val fToken = userRepository.getFirebaseToken()
        usersTableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.USERS
            } else {
                TableName.USERS_DEV
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
        usersTableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE /*"https://sheperdstagging.itechnolabs.tech/"*/) {
                TableName.USERS
            } else {
                TableName.USERS_DEV
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


    // Save Enterprise Detail
    fun saveEnterpriseDetail(enterprise: Enterprise) {
        userRepository.saveEnterpriseDetail(enterprise)
    }

    // Get Enterprise Detail
    fun getEnterpriseDetail(): Enterprise? {
        return userRepository.getEnterpriseDetail()
    }
}