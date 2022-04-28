package com.app.shepherd.ui.component.home.viewModel

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.app.shepherd.R
import com.app.shepherd.data.DataRepository
import com.app.shepherd.data.dto.dashboard.DashboardModel
import com.app.shepherd.data.dto.menuItem.MenuItemModel
import com.app.shepherd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel  @Inject constructor(private val dataRepository: DataRepository) :
    BaseViewModel() {

    var menuItemList: ArrayList<MenuItemModel> = ArrayList()
    var menuItemMap: HashMap<String, ArrayList<MenuItemModel>> = HashMap()

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
                icon = ContextCompat.getDrawable(context, com.lassi.R.drawable.shape_circle_white)!!,
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

}