package com.app.shepherd.ui.component.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.app.shepherd.R
import com.app.shepherd.ShepherdApp
import com.app.shepherd.data.dto.user.UserProfiles
import com.app.shepherd.databinding.ActivityHomeBinding
import com.app.shepherd.network.retrofit.DataResult
import com.app.shepherd.network.retrofit.observeEvent
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.base.listeners.ChildFragmentToActivityListener
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.utils.Const
import com.app.shepherd.utils.Prefs
import com.app.shepherd.utils.extensions.showError
import com.app.shepherd.utils.extensions.showSuccess
import com.app.shepherd.view_model.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_dashboard.*

@AndroidEntryPoint
class HomeActivity : BaseActivity(), ChildFragmentToActivityListener,
    View.OnClickListener {

    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val TAG = "HomeActivity"
    private var profilePicLovedOne: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getHomeData()
        setupNavigationDrawer()

        setMenuItemAdapter()
        binding.listener = this
        binding.appBarDashboard.ivMenu.setOnClickListener(View.OnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        })
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.appBarDashboard.ivLovedOneProfile.setOnClickListener {
            navController.navigate(R.id.nav_loved_one)
        }
    }


    override fun observeViewModel() {
        // Observe Logout Response
        viewModel.logoutResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {

                    navigateToLoginScreen()
                }
            }
        }

        // Observe Get Home Data Api Response
        viewModel.homeResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(this, it.message.toString())
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val lovedOneProfilePic = it.data.payload?.lovedOneUserProfile
                    Picasso.get().load(lovedOneProfilePic)
                        .placeholder(R.drawable.ic_defalut_profile_pic)
                        .into(ivLovedOneProfile)
                //save data
                    viewModel.saveLovedUser(it.data.payload!!.careTeamProfiles[0].loveUser)
                }
            }
        }


        // Observe Loved One Detail
        /*viewModel.lovedOneDetailsLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(this, it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    profilePicLovedOne = it.data.payload?.userProfiles?.profilePhoto

                    Picasso.get().load(profilePicLovedOne).placeholder(R.drawable.test_image)
                        .into(ivLovedOneProfile)
                }
            }
        }*/
    }


    override fun initViewBinding() {

    }


    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)

        // Get Logged In User's profile info
        val loggedInUser = Prefs.with(ShepherdApp.appContext)?.getObject(
            Const.USER_DETAILS,
            UserProfiles::class.java
        )

        val firstName = loggedInUser?.firstname
        val lastName = loggedInUser?.lastname
        val fullName = "$firstName $lastName"

        val profilePicLoggedInUser = loggedInUser?.profilePhoto

        binding.ivName.text = fullName

        Picasso.get().load(profilePicLoggedInUser).placeholder(R.drawable.ic_defalut_profile_pic)
            .into(binding.ivLoggedInUserProfile)

        // Set User's Role
        val role = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_ROLE, "")
        if (role.isNullOrEmpty()) {
            binding.tvRole.text = getString(R.string.care_team_leader)
        } else {
            binding.tvRole.text = role
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->

            binding.appBarDashboard.ivEditProfile.isVisible =
                destination.id == R.id.nav_profile
            binding.appBarDashboard.ivSetting.isVisible =
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
                        ivSetting.setOnClickListener() {
                            navController.navigate(R.id.nav_setting)
                        }
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_vital_stats -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.vital_stats)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.nav_add_vitals)
                            }
                        }
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
                R.id.nav_care_points -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.care_points)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.nav_add_new_event)
                            }
                        }
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_care_team -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.careteam)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.nav_add_care_team_member)
                                lockUnlockDrawer(true)
                                clTopWrapper.isVisible = false
                            }
                        }
                    }
                }
                else -> {
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
            R.id.tvLogout -> {
                viewModel.logOut()
            }
        }
        drawerLayout?.closeDrawer(GravityCompat.START)
    }

    fun navigateToLoginScreen() {
        //startActivityWithFinish<LoginActivity>()
        showSuccess(this, "User logged out successfully")
        Prefs.with(ShepherdApp.appContext)?.removeAll()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    override fun msgFromChildFragmentToActivity() {
        viewModel.getHomeData()
    }

}