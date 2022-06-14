package com.app.shepherd.ui.component.home

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
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
class HomeActivity : BaseActivity(),
    View.OnClickListener {

    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationDrawer()

        setMenuItemAdapter()
        binding.listener = this
        binding.appBarDashboard.ivMenu.setOnClickListener(View.OnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        })
    }


    override fun observeViewModel() {
    }


    override fun initViewBinding() {

    }


    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.appBarDashboard.ivEditProfile.isVisible =
                destination.id == R.id.nav_profile
            when (destination.id) {
                R.id.nav_dashboard -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.home)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = true
                        tvNew.isVisible = false
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_my_medlist -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.medlist_reminder)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.nav_add_new_medication)
                            }
                        }
                    }
                    lockUnlockDrawer(false)
                }

                R.id.nav_messages -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.discussions)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.nav_new_message)
                            }
                        }
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_profile -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.my_profile)
                        clTopWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.isVisible = false
                        ivEditProfile.setOnClickListener() {
                            navController.navigate(R.id.nav_edit_profile)
                        }
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_vital_stats -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.vital_stats)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = false
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_resources -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.resources)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = false
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_schedule_medication,
                R.id.nav_med_detail,
                R.id.nav_secure_code,
                R.id.nav_change_password,
                R.id.nav_edit_profile,
                R.id.nav_chat,
                R.id.nav_new_message,
                R.id.nav_medication_details,
                R.id.nav_add_new_medication -> {
                    lockUnlockDrawer(true)
                    binding.appBarDashboard.apply {
                        clTopWrapper.isVisible = false
                    }
                }
            }
        }
    }

    private fun lockUnlockDrawer(lock: Boolean) {
        binding.drawerLayout.apply {
            if (lock) {
                setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    private fun setMenuItemAdapter() {
        viewModel.inflateDashboardList(this)
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            navController,
            appBarConfiguration
        ) || super.onSupportNavigateUp()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.clProfileWrapper -> {
                navController.navigate(R.id.nav_profile)
            }
            R.id.llHome -> {
                navController.navigate(R.id.nav_dashboard)
            }
            R.id.llCarePoint -> {
                navController.navigate(R.id.nav_care_points)
            }
            R.id.llDiscussions -> {
                navController.navigate(R.id.nav_messages)
            }
            R.id.llMedList -> {
                navController.navigate(R.id.nav_my_medlist)
            }
            R.id.llResources -> {
                navController.navigate(R.id.nav_resources)
            }
            R.id.llLockBox -> {
                navController.navigate(R.id.nav_lock_box)
            }
            R.id.llVitalStats -> {
                navController.navigate(R.id.nav_vital_stats)
            }
            R.id.llCareTeam -> {
                navController.navigate(R.id.nav_care_team)
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
    }
}