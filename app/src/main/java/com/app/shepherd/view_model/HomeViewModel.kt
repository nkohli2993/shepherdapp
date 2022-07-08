package com.app.shepherd.view_model

import android.content.Context
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.shepherd.R
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.dashboard.HomeResponseModel
import com.app.shepherd.data.dto.menuItem.MenuItemModel
import com.app.shepherd.data.dto.user.UserDetailsResponseModel
import com.app.shepherd.data.local.UserRepository
import com.app.shepherd.data.remote.auth_repository.AuthRepository
import com.app.shepherd.data.remote.home_repository.HomeRepository
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.Event
import com.app.shepherd.ui.base.BaseResponseModel
import com.app.shepherd.ui.base.BaseViewModel
import com.app.shepherd.utils.SingleEvent
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
    private fun getLovedOneUserId(): Int {
        val userLovedOneArray = userRepository.getPayload()?.userLovedOne
        return userLovedOneArray?.get(0)?.loveUserId ?: 0
    }

    fun getHomeData(): LiveData<Event<DataResult<HomeResponseModel>>> {
        val lovedOneId = userRepository.getLovedOneId()
        val status = 1
       // Log.d(TAG, "LovedOneID :$lovedOneId ")
        viewModelScope.launch {
            val response = homeRepository.getHomeData(lovedOneId, status)
            withContext(Dispatchers.Main) {
                response.collect {
                    _homeResponseLiveData.postValue(Event(it))
                }
            }
        }
        return homeResponseLiveData
    }


}