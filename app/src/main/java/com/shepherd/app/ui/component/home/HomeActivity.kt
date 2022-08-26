package com.shepherd.app.ui.component.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.shepherd.app.BuildConfig
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.user.UserProfiles
import com.shepherd.app.databinding.ActivityHomeBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherd.app.ui.component.carePoints.CarePointsFragment
import com.shepherd.app.ui.component.careTeamMembers.CareTeamMembersFragment
import com.shepherd.app.ui.component.dashboard.DashboardFragment
import com.shepherd.app.ui.component.lockBox.LockBoxFragment
import com.shepherd.app.ui.component.login.LoginActivity
import com.shepherd.app.ui.component.messages.MessagesFragment
import com.shepherd.app.ui.component.myMedList.MyMedListFragment
import com.shepherd.app.ui.component.profile.ProfileFragment
import com.shepherd.app.ui.component.resources.ResourcesFragment
import com.shepherd.app.ui.component.settings.SettingFragment
import com.shepherd.app.ui.component.vital_stats.VitalStatsFragment
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Modules
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showSuccess
import com.shepherd.app.view_model.HomeViewModel
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
    val viewModel: HomeViewModel by viewModels()
    private val TAG = "HomeActivity"
    private var profilePicLovedOne: String? = null


    @SuppressLint("SetTextI18n")
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

//         permissionShowHide(View.VISIBLE)
        // show accessed cards only to users
        if (!viewModel.getUUID().isNullOrEmpty() && viewModel.getLovedUserDetail() != null) {
            if (viewModel.getUUID() == viewModel.getLovedUserDetail()?.userId)
                if (viewModel.getLovedUserDetail() != null) {
                    val perList =
                        viewModel.getLovedUserDetail()?.permission?.split(',')?.map { it.trim() }
                    permissionShowHide(View.GONE)
                    for (i in perList?.indices!!) {
                        checkPermission(perList[i].toInt())
                    }
                } else {
                    permissionShowHide(View.VISIBLE)
                }
        } else {
            permissionShowHide(View.VISIBLE)
        }

        binding.tvVersion.text = "V: ${BuildConfig.VERSION_NAME}"

    }

    private fun permissionShowHide(value: Int) {
        binding.llCarePoint.visibility = value
        binding.llLockBox.visibility = value
        binding.llMedList.visibility = value
        binding.llResources.visibility = value
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

                R.id.nav_lock_box -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.lockbox)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        tvNew.apply {
                            isVisible = true
                            setOnClickListener {
                                navController.navigate(R.id.addNewLockBoxFragment, null)
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

    private var backPressed: Long = 0

    override fun onBackPressed() {
        // to check navigation fragments
        val navHostFragment = supportFragmentManager.primaryNavigationFragment as NavHostFragment?
        val fragmentManager: FragmentManager = navHostFragment!!.childFragmentManager
        when (fragmentManager.primaryNavigationFragment!!) {
            is CarePointsFragment, is MyMedListFragment, is LockBoxFragment, is CareTeamMembersFragment,
            is ResourcesFragment, is VitalStatsFragment, is MessagesFragment, is ProfileFragment -> {
                findNavController(R.id.nav_host_fragment_content_dashboard).navigate(R.id.nav_dashboard)
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            is SettingFragment -> {
                findNavController(R.id.nav_host_fragment_content_dashboard).navigate(R.id.nav_profile)
            }
            is DashboardFragment -> {
                if (backPressed + 2000 > System.currentTimeMillis()) super.onBackPressed()
                else Toast.makeText(
                    applicationContext,
                    "Press once again to exit!",
                    Toast.LENGTH_SHORT
                ).show()
                backPressed = System.currentTimeMillis()
            }
            else -> {
                super.onBackPressed()
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }

        }


    }

    private fun checkPermission(permission: Int?) {
        when {
            Modules.CareTeam.value == permission -> {
                binding.llCarePoint.visibility = View.VISIBLE
            }
            Modules.LockBox.value == permission -> {
                binding.llLockBox.visibility = View.VISIBLE
            }
            Modules.MedList.value == permission -> {
                binding.llMedList.visibility = View.VISIBLE
            }
            Modules.Resources.value == permission -> {
                binding.llResources.visibility = View.VISIBLE
            }
        }

    }


}