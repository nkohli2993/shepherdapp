package com.app.shepherd.ui.component.home

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityHomeBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.adapter.MenuItemAdapter
import com.app.shepherd.ui.component.home.viewModel.HomeViewModel
import com.app.shepherd.utils.SingleEvent
import com.app.shepherd.utils.observeEvent
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setSupportActionBar(binding.appBarDashboard.toolbar)

        setupNavigationDrawer()

        setMenuItemAdapter()
    }


    override fun observeViewModel() {
        observeEvent(viewModel.selectedDrawerItem, ::navigateToDashboardItems)
    }


    override fun initViewBinding() {
    }


    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout

//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            binding.appBarDashboard.toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout!!.addDrawerListener(toggle)
//        toggle.isDrawerIndicatorEnabled = true
//        toggle.syncState()

//        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)


//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_dashboard, R.id.nav_notifications, R.id.nav_profile,
//                R.id.nav_care_team, R.id.nav_care_points, R.id.nav_lock_box,
//                R.id.nav_my_medlist, R.id.nav_messages, R.id.nav_resources,
//            ), drawerLayout
//        )

//        navView.setNavigationItemSelectedListener(this)

//        NavigationUI.setupWithNavController(navView, navController)
//        NavigationUI.setupActionBarWithNavController(this, navController)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

//        binding.appBarDashboard.toolbar.setupWithNavController(navController, drawerLayout)

    }

    private fun setMenuItemAdapter() {
        viewModel.inflateDashboardList(this)

//        val adapter = MenuItemAdapter(viewModel, viewModel.menuItemList, viewModel.menuItemMap)
//        binding.recyclerViewMenu.adapter = adapter
    }


    private fun navigateToDashboardItems(navigateEvent: SingleEvent<String>) {
        navigateEvent.getContentIfNotHandled()?.let {
            when (it) {
                resources.getString(R.string.care_team) -> {
                    navController.navigate(R.id.nav_care_team)
                }
                resources.getString(R.string.care_points) -> {
                    navController.navigate(R.id.nav_care_points)
                }
                resources.getString(R.string.lock_box) -> {
                    navController.navigate(R.id.nav_lock_box)
                }
                resources.getString(R.string.medlist) -> {
                    navController.navigate(R.id.nav_my_medlist)
                }
                resources.getString(R.string.messages) -> {
                    navController.navigate(R.id.nav_messages)
                }
                resources.getString(R.string.resources) -> {
                    navController.navigate(R.id.nav_resources)
                }
                resources.getString(R.string.profile) -> {
                    navController.navigate(R.id.nav_profile)
                }
                resources.getString(R.string.notifications) -> {
                    navController.navigate(R.id.nav_notifications)
                }

                resources.getString(R.string.add_loved_one) -> {
                    navController.navigate(R.id.nav_add_loved_one)
                }
            }

        }
        drawerLayout?.closeDrawer(GravityCompat.START)
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
}