package com.shepherdapp.app.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneModel
import com.shepherdapp.app.data.dto.add_loved_one.CreateLovedOneResponseModel
import com.shepherdapp.app.data.dto.add_loved_one.UploadPicResponseModel
import com.shepherdapp.app.data.dto.edit_loved_one.EditLovedOneResponseModel
import com.shepherdapp.app.data.dto.relation.RelationResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.relation_repository.RelationRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by Deepak Rattan on 06/06/22
 */
@HiltViewModel
class AddLovedOneViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val relationRepository: RelationRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    var imageFile: File? = null
    private var _uploadImageLiveData = MutableLiveData<Event<DataResult<UploadPicResponseModel>>>()
    var uploadImageLiveData: LiveData<Event<DataResult<UploadPicResponseModel>>> =
        _uploadImageLiveData

    private var _relationResponseLiveData =
        MutableLiveData<Event<DataResult<RelationResponseModel>>>()
    var relationResponseLiveData: LiveData<Event<DataResult<RelationResponseModel>>> =
        _relationResponseLiveData


    private var _createLovedOneLiveData =
        MutableLiveData<Event<DataResult<CreateLovedOneResponseModel>>>()
    var createLovedOneLiveData: LiveData<Event<DataResult<CreateLovedOneResponseModel>>> =
        _createLovedOneLiveData

    private var _editLovedOneLiveData =
        MutableLiveData<Event<DataResult<EditLovedOneResponseModel>>>()
    var editLovedOneLiveData: LiveData<Event<DataResult<EditLovedOneResponseModel>>> =
        _editLovedOneLiveData


    var createLovedOneData = MutableLiveData<CreateLovedOneModel>().apply {
        value = CreateLovedOneModel()
    }


    // Get Relations
    fun getRelations(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<RelationResponseModel>>> {
        viewModelScope.launch {
            val response = relationRepository.getRelations(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect { _relationResponseLiveData.postValue(Event(it)) }
            }
        }
        return relationResponseLiveData
    }


    //Upload Image
    fun uploadImage(file: File?): LiveData<Event<DataResult<UploadPicResponseModel>>> {
        viewModelScope.launch {
            val response = authRepository.uploadImage(file)
            withContext(Dispatchers.Main) {
                response.collect { _uploadImageLiveData.postValue(Event(it)) }
            }
        }
        /* Handler(Looper.getMainLooper()).postDelayed({
             // Your Code
             uploadImageLiveData = _uploadImageLiveData
         }, 3000)*/

        return uploadImageLiveData
    }

    // Create Loved One
    fun createLovedOne(
        email: String?,
        firstname: String,
        lastname: String?,
        relation_id: Int?,
        phone_code: String?,
        dob: String?,
        place_id: String?,
        customAddress: String?,
        phone_no: String?,
        profile_photo: String?,
        sendInvitation: Boolean?
    ): LiveData<Event<DataResult<CreateLovedOneResponseModel>>> {
        createLovedOneData.value.let {
            it?.email = email
            it?.firstname = firstname
            it?.lastname = lastname
            it?.relationId = relation_id
            it?.phoneCode = phone_code
            it?.dob = dob
            it?.placeId = place_id
            it?.customAddress = customAddress
            it?.phoneNo = phone_no
            it?.profilePhoto = profile_photo
            it?.sendInvitation = sendInvitation
        }


        viewModelScope.launch {
            val response = createLovedOneData.value?.let { relationRepository.createLovedOne(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _createLovedOneLiveData.postValue(Event(it))
                }
            }
        }
        return createLovedOneLiveData
    }

    // Edit Loved One
    fun editLovedOne(
        email: String?,
        firstname: String,
        lastname: String?,
        relation_id: Int?,
        phone_code: String?,
        dob: String?,
        place_id: String?,
        customAddress: String?,
        phone_no: String?,
        profile_photo: String?,
        uuid: String?,
        sendInvitation: Boolean?
    ): LiveData<Event<DataResult<EditLovedOneResponseModel>>> {
        createLovedOneData.value.let {
            it?.email = email
            it?.firstname = firstname
            it?.lastname = lastname
            it?.relationId = relation_id
            it?.phoneCode = phone_code
            it?.dob = dob
            it?.placeId = place_id
            it?.customAddress = customAddress
            it?.phoneNo = phone_no
            it?.profilePhoto = profile_photo
            it?.sendInvitation = sendInvitation
        }


        viewModelScope.launch {
            val response =
                createLovedOneData.value?.let {
                    uuid?.let { it1 ->
                        relationRepository.editLovedOne(
                            it1, it
                        )
                    }
                }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _editLovedOneLiveData.postValue(Event(it))
                }
            }
        }
        return editLovedOneLiveData
    }

    fun saveLovedOneId(lovedOneID: String?) {
        lovedOneID?.let { userRepository.saveLovedOneId(it) }
    }

    fun saveLovedOneUUID(lovedOneUUID: String) {
        userRepository.saveLovedOneUUId(lovedOneUUID)
    }

    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
    }

    fun getLoggedInUserUUID(): String? {
        return userRepository.getUUID()
    }

}