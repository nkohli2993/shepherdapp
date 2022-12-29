package com.shepherdapp.app.view_model

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.medical_conditions.get_loved_one_medical_conditions.GetLovedOneMedicalConditionsResponseModel
import com.shepherdapp.app.data.dto.resource.*
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.resource.ResourceRepository
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
import javax.inject.Inject

@HiltViewModel
class ResourceViewModel @Inject constructor(
    private val dataRepository: ResourceRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseViewModel() {

    // Resource list Response Live Data
    private var _resourceResponseLiveData =
        MutableLiveData<Event<DataResult<ResponseRelationModel>>>()
    var resourceResponseLiveData: LiveData<Event<DataResult<ResponseRelationModel>>> =
        _resourceResponseLiveData

    // Resource list Response particular id based Live Data
    private var _resourceIdBasedResponseLiveData =
        MutableLiveData<Event<DataResult<ParticularResourceResponseModel>>>()
    var resourceIdBasedResponseLiveData: LiveData<Event<DataResult<ParticularResourceResponseModel>>> =
        _resourceIdBasedResponseLiveData

    // Resource list Response Live Data
    private var _trendingResourceResponseLiveData =
        MutableLiveData<Event<DataResult<ResponseRelationModel>>>()
    var trendingResourceResponseLiveData: LiveData<Event<DataResult<ResponseRelationModel>>> =
        _trendingResourceResponseLiveData

    // selected resource
    private val _selectedResourceDetail = MutableLiveData<SingleEvent<AllResourceData>>()
    val selectedResourceDetail: LiveData<SingleEvent<AllResourceData>> get() = _selectedResourceDetail

    // selected medical category TAG
    private val _selectedMedicalCategoryTAG = MutableLiveData<SingleEvent<CategoryData>>()
    val selectedMedicalCategoryTAG: LiveData<SingleEvent<CategoryData>> get() = _selectedMedicalCategoryTAG

    private var _userDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailByUUIDLiveData


    fun openSelectedResource(position: AllResourceData) {
        _selectedResourceDetail.value = SingleEvent(position)
    }

    fun openSelectedMedicalCategoryTag(category: CategoryData) {
        _selectedMedicalCategoryTAG.value = SingleEvent(category)
    }


    private var _lovedOneMedicalConditionResponseLiveData =
        MutableLiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>>()
    var lovedOneMedicalConditionResponseLiveData: LiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>> =
        _lovedOneMedicalConditionResponseLiveData

    private var _categoryResourceResponseLiveData =
        MutableLiveData<Event<DataResult<GetCategoriesResponseModel>>>()
    var categoryResourceResponseLiveData: LiveData<Event<DataResult<GetCategoriesResponseModel>>> =
        _categoryResourceResponseLiveData


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showSnackBarPrivate = MutableLiveData<SingleEvent<Any>>()
    val showSnackBar: LiveData<SingleEvent<Any>> get() = showSnackBarPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }

    fun getLovedOneUUId() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.LOVED_ONE_UUID, "")

    fun getUserDetail(): UserProfile? {
        return userRepository.getCurrentUser()
    }

    fun getAllResourceApi(
        pageNumber: Int,
        limit: Int,
    ): LiveData<Event<DataResult<ResponseRelationModel>>> {
        viewModelScope.launch {
            val response =
                dataRepository.getAllResourceApi(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _resourceResponseLiveData.postValue(Event(it))
                }
            }
        }
        return resourceResponseLiveData
    }

    fun getAllResourceAsPerCategoryApi(
        pageNumber: Int,
        limit: Int,
        categoryIds: String
    ): LiveData<Event<DataResult<ResponseRelationModel>>> {
        viewModelScope.launch {
            val response =
                dataRepository.getAllResourceAsPerCategory(pageNumber, limit, categoryIds)
            withContext(Dispatchers.Main) {
                response.collect {
                    _resourceResponseLiveData.postValue(Event(it))
                }
            }
        }
        return resourceResponseLiveData
    }

    fun getSearchResourceResultApi(
        pageNumber: Int,
        limit: Int,
//        lovedOneId: String,
//        conditions: String,
        search: String
    ): LiveData<Event<DataResult<ResponseRelationModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getSearchResourceResultApi(
                pageNumber,
                limit,
//                lovedOneId,
//                conditions,
                search
            )
            withContext(Dispatchers.Main) {
                response.collect {
                    _resourceResponseLiveData.postValue(Event(it))
                }
            }
        }
        return resourceResponseLiveData
    }

    fun getResourceCategories(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<GetCategoriesResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getResourceCategories(
                pageNumber,
                limit
            )
            withContext(Dispatchers.Main) {
                response.collect {
                    _categoryResourceResponseLiveData.postValue(Event(it))
                }
            }
        }
        return categoryResourceResponseLiveData
    }

    fun getResourceDetail(
        id: Int
    ): LiveData<Event<DataResult<ParticularResourceResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getResourceDetail(id)
            withContext(Dispatchers.Main) {
                response.collect {
                    _resourceIdBasedResponseLiveData.postValue(Event(it))
                }
            }
        }
        return resourceIdBasedResponseLiveData
    }

    fun getTrendingResourceApi(
        pageNumber: Int,
        limit: Int
    ): LiveData<Event<DataResult<ResponseRelationModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getTrendingResourceApi(pageNumber, limit)
            withContext(Dispatchers.Main) {
                response.collect {
                    _trendingResourceResponseLiveData.postValue(Event(it))
                }
            }
        }
        return trendingResourceResponseLiveData
    }

    // Get Loved One's Medical Conditions
    fun getLovedOneMedicalConditions(lovedOneUUID: String): LiveData<Event<DataResult<GetLovedOneMedicalConditionsResponseModel>>> {
        viewModelScope.launch {
            val response = dataRepository.getLovedOneMedicalConditions(lovedOneUUID)
            withContext(Dispatchers.Main) {
                response.collect {
                    _lovedOneMedicalConditionResponseLiveData.postValue(Event(it))
                }
            }
        }
        return lovedOneMedicalConditionResponseLiveData
    }

    // Get User Details
    fun getUserDetailByUUID(): LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> {
        val uuid = userRepository.getLovedOneUUId()
        viewModelScope.launch {
            val response = uuid?.let { authRepository.getUserDetailsByUUID(it) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _userDetailByUUIDLiveData.postValue(Event(it))
                }
            }
        }
        return userDetailByUUIDLiveData
    }

    fun saveCategoryIds(categoryIds: ArrayList<Int>) {
        userRepository.saveCategoryIds(categoryIds)
    }

    fun getCategoryIds(): java.util.ArrayList<Int?>? {
        return userRepository.getCategoryIds()
    }

    fun saveCategoryDataList(categoryDataList: ArrayList<CategoryData>) {
        userRepository.saveCategoryDataList(categoryDataList)
    }

    fun getCategoryDataList(): java.util.ArrayList<CategoryData?>? {
        return userRepository.getCategoryDataList()
    }

}