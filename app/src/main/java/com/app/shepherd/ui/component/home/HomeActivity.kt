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
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityHomeBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.home.adapter.MenuItemAdapter
import com.app.shepherd.ui.component.home.viewModel.HomeViewModel
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

        setSupportActionBar(binding.appBarDashboard.toolbar)

        setupNavigationDrawer()

        setMenuItemAdapter()
    }


    override fun observeViewModel() {
    }


    override fun initViewBinding() {
    }


    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarDashboard.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout!!.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()

        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_dashboard, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )

        navView.setNavigationItemSelectedListener(this)

        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        binding.appBarDashboard.toolbar.setupWithNavController(navController,drawerLayout)

    }

    private fun setMenuItemAdapter() {
        viewModel.inflateDashboardList(this)

        val adapter = MenuItemAdapter(viewModel.menuItemList, viewModel.menuItemMap)
        binding.recyclerViewMenu.adapter = adapter
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }
}