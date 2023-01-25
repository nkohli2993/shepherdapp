package com.shepherdapp.app.view_model

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.dashboard.HomeResponseModel
import com.shepherdapp.app.data.dto.dashboard.LoveUser
import com.shepherdapp.app.data.dto.login.UserLovedOne
import com.shepherdapp.app.data.dto.menuItem.MenuItemModel
import com.shepherdapp.app.data.dto.user.UserDetailsResponseModel
import com.shepherdapp.app.data.dto.user_detail.UserDetailByUUIDResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.auth_repository.AuthRepository
import com.shepherdapp.app.data.remote.home_repository.HomeRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseResponseModel
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.TableName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val homeRepository: HomeRepository
) : BaseViewModel() {
    var usersTableName: String? = null

    var menuItemList: ArrayList<MenuItemModel> = ArrayList()
    var menuItemMap: HashMap<String, ArrayList<MenuItemModel>> = HashMap()

    private var _logoutResponseLiveData = MutableLiveData<Event<DataResult<BaseResponseModel>>>()

    var logoutResponseLiveData: LiveData<Event<DataResult<BaseResponseModel>>> =
        _logoutResponseLiveData

    private var _lovedOneDetailsLiveData =
        MutableLiveData<Event<DataResult<UserDetailsResponseModel>>>()
    var lovedOneDetailsLiveData: LiveData<Event<DataResult<UserDetailsResponseModel>>> =
        _lovedOneDetailsLiveData

    private var _homeResponseLiveData =
        MutableLiveData<Event<DataResult<HomeResponseModel>>>()
    var homeResponseLiveData: LiveData<Event<DataResult<HomeResponseModel>>> =
        _homeResponseLiveData

    private var _userDetailByUUIDLiveData =
        MutableLiveData<Event<DataResult<UserDetailByUUIDResponseModel>>>()
    var userDetailByUUIDLiveData: LiveData<Event<DataResult<UserDetailByUUIDResponseModel>>> =
        _userDetailByUUIDLiveData


    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private val selectedDrawerItemPrivate = MutableLiveData<SingleEvent<String>>()
    val selectedDrawerItem: LiveData<SingleEvent<String>> get() = selectedDrawerItemPrivate

    fun inflateDashboardList(context: Context) {
        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.add_loved_one)
            )
        )

        menuItemMap[context.getString(R.string.add_loved_one)] = ArrayList()


        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.title_activity_dashboard)
            )
        )

        menuItemMap[context.getString(R.string.title_activity_dashboard)] = arrayListOf(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.care_team)
            ),
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.care_points)
            ),
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.lock_box)
            ),
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.medlist)
            ),
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.messages)
            ),
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.resources)
            ),
        )


        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.notifications)
            )
        )

        menuItemMap[context.getString(R.string.notifications)] = ArrayList()


        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.profile)
            )
        )

        menuItemMap[context.getString(R.string.profile)] = ArrayList()

        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(
                    context,
                    com.lassi.R.drawable.shape_circle_white
                )!!,
                title = context.getString(R.string.empty)
            )
        )

        menuItemMap[context.getString(R.string.empty)] = ArrayList()

        menuItemList.add(
            MenuItemModel(
                icon = ContextCompat.getDrawable(context, R.drawable.ic_heart_outline)!!,
                title = context.getString(R.string.logout)
            )
        )

        menuItemMap[context.getString(R.string.logout)] = ArrayList()


    }

    fun onDrawerItemSelected(title: String) {
        selectedDrawerItemPrivate.value = SingleEvent(title)
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

    fun saveLovedUser(user: LoveUser?) {
        Prefs.with(ShepherdApp.appContext)!!.save(Const.LOVED_USER_DETAILS, user)
    }
    // Get User Details
    /* fun getLovedOneDetails(lovedOneUserId: Int): LiveData<Event<DataResult<UserDetailsResponseModel>>> {
         //val userID = getLovedOneUserId()
         viewModelScope.launch {
             val response = authRepository.getUserDetails(lovedOneUserId)
             withContext(Dispatchers.Main) {
                 response.collect {
                     _lovedOneDetailsLiveData.postValue(Event(it))
                 }
             }
         }
         return lovedOneDetailsLiveData
     }*/

    //get userID from Shared Pref
    /* private fun getLovedOneUserId(): Int {
         val userLovedOneArray = userRepository.getPayload()?.userLovedOne
         return userLovedOneArray?.get(0)?.loveUserId ?: 0
     }*/


    fun getHomeData(): LiveData<Event<DataResult<HomeResponseModel>>> {
        val lovedOneUUID = userRepository.getLovedOneUUId()
        val status = 1
        // Log.d(TAG, "LovedOneID :$lovedOneId ")
        viewModelScope.launch {
            val response = lovedOneUUID?.let { homeRepository.getHomeData(it, status) }
            withContext(Dispatchers.Main) {
                response?.collect {
                    _homeResponseLiveData.postValue(Event(it))
                }
            }
        }
        return homeResponseLiveData
    }

    //get userinfo from Shared Pref
    fun getLovedUserDetail(): UserLovedOne? {
        return userRepository.getLovedOneUserDetail()
    }

    fun getUUID() = Prefs.with(ShepherdApp.appContext)!!.getString(Const.UUID, "")

    fun getLovedOneUUID() = userRepository.getLovedOneUUId()

    fun isLoggedInUserLovedOne(): Boolean? {
        return userRepository.isLoggedInUserLovedOne()
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

    // Clear Firebase Token on logout
    fun clearFirebaseToken() {
        usersTableName =
            if (BuildConfig.BASE_URL == Const.BASE_URL_LIVE/*"https://sheperdstagging.itechnolabs.tech/"*/) {
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

    fun getLovedOnePic(): String? {
        return userRepository.getLovedOneProfilePic()
    }

    fun isLoggedInUserCareTeamLead(): Boolean? {
        return userRepository.isLoggedInUserTeamLead()
    }
}