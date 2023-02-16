package com.shepherdapp.app.ui.component.home

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.user.UserProfiles
import com.shepherdapp.app.databinding.ActivityHomeBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.base.listeners.ChildFragmentToActivityListener
import com.shepherdapp.app.ui.base.listeners.ChildSourceToActivityListener
import com.shepherdapp.app.ui.base.listeners.UpdateViewOfParentListener
import com.shepherdapp.app.ui.component.carePoints.CarePointsFragment
import com.shepherdapp.app.ui.component.careTeamMembers.CareTeamMembersFragment
import com.shepherdapp.app.ui.component.dashboard.DashboardFragment
import com.shepherdapp.app.ui.component.dashboard.DashboardFragmentDirections
import com.shepherdapp.app.ui.component.lockBox.LockBoxFragment
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.ui.component.messages.MessagesFragment
import com.shepherdapp.app.ui.component.myMedList.MyMedListFragment
import com.shepherdapp.app.ui.component.profile.ProfileFragment
import com.shepherdapp.app.ui.component.resources.ResourcesFragment
import com.shepherdapp.app.ui.component.settings.SettingFragment
import com.shepherdapp.app.ui.component.vital_stats.VitalStatsFragment
import com.shepherdapp.app.utils.*
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.HomeViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar_dashboard.*
import kotlinx.android.synthetic.main.content_dashboard.*

@AndroidEntryPoint
class HomeActivity : BaseActivity(), ChildFragmentToActivityListener,
    View.OnClickListener, UpdateViewOfParentListener {

    private lateinit var navController: NavController
    private var drawerLayout: DrawerLayout? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    val viewModel: HomeViewModel by viewModels()
    private val TAG = "HomeActivity"
    private var profilePicLovedOne: String? = null
    private var permissions: String? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check fro POST Notification permission in Android 13 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!checkNotificationPermission()) {
                requestPostNotificationPermission()
            }
        }

        // Handle Push Notification Data
        /* if (intent != null && intent.hasExtra("isNotification")) {
             navController = findNavController(R.id.nav_host_fragment_content_dashboard)
             checkNotificationAction(intent.getBundleExtra("detail"))
         }*/

        if (intent != null && intent.hasExtra("source")) {
            if (intent.getStringExtra("source") == "LovedOne Screen") {
                // Redirect to LovedOnes Fragment
                findNavController(R.id.nav_host_fragment_content_dashboard).navigate(R.id.nav_loved_one)
            }
        }
        showLoading("")
        viewModel.getHomeData()
        viewModel.getUserDetailByUUID()
        setupNavigationDrawer()

        setMenuItemAdapter()
        binding.listener = this
        binding.appBarDashboard.ivMenu.setOnClickListener(View.OnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START, true)
        })
        setOnClickListeners()

//        permissionShowHide(View.VISIBLE)
        // show accessed cards only to users
        /* if (!viewModel.getUUID().isNullOrEmpty() && viewModel.getLovedUserDetail() != null) {
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
         }*/

        binding.tvVersion.text = "V: ${BuildConfig.VERSION_NAME}"

    }

    private fun checkNotificationAction(bundle: Bundle?) {
        val type = bundle?.get("type") as String?

        when (type) {
            // Handle Chat Message Notification
            Const.NotificationAction.MESSAGE -> {
                val chatId = (bundle?.get("chat_id") as String)
                /* val chatData = ChatListData().apply {
                     chatType = (bundle.get("chat_type") as String?)?.toIntOrNull()
                     if (chatType == Chat.CHAT_GROUP) {
                         toUser = ChatUserDetail(
                             id = (bundle.get("user_id") as String?) ?: "",
                             imageUrl = (bundle.get("from_image") as String?) ?: "",
                             name = (bundle.get("from_name") as String?) ?: ""
                         )
                     } else {
                         groupName = bundle.get("from_name") as String?
                     }

                     id = chatId
                 }*/
                val eventId = bundle.getString("group_id")
                Log.d(TAG, "checkNotificationAction: eventId :$eventId")

                val navDirection = eventId?.toInt()?.let {
                    DashboardFragmentDirections.actionNavDashboardToNavCarePointsDetail(
                        "Home Screen",
                        it
                    )
                }
                navDirection?.let { navController.navigate(it) }
                clearNotification()
            }
            // Handle Care Point Push Notification
            Const.NotificationAction.CARE_POINT_CREATED -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "CarePoint Push Notification :EventId $eventID ")
                // Redirect to CarePoints Screen
                navController.navigate(R.id.nav_care_points)
                clearNotification()
            }
            // Handle MedList Created Push notifications
            Const.NotificationAction.MEDICATION_CREATED -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "MedList Created Push Notification :EventId $eventID ")
                navController.navigate(R.id.nav_my_medlist)
                clearNotification()
            }
            // Handle MedList Updated Push notifications
            Const.NotificationAction.MEDICATION_UPDATED -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "MedList Updated Push Notification :EventId $eventID ")
                navController.navigate(R.id.nav_my_medlist)
                clearNotification()
            }
            // Handle LockBox Created Push notifications
            Const.NotificationAction.LOCK_BOX_CREATED -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "LockBox Created Push Notification :EventId $eventID ")
                navController.navigate(R.id.nav_lock_box)
                clearNotification()
            }
            // Handle LockBox Updated Push notifications
            Const.NotificationAction.LOCK_BOX_UPDATED -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "LockBox Created Push Notification :EventId $eventID ")
                navController.navigate(R.id.nav_lock_box)
                clearNotification()
            }
            // Handle CareTeam Invite Push notifications
            Const.NotificationAction.CARE_TEAM_INVITE -> {
                val eventID = bundle?.getString("event_id")
                Log.d(TAG, "LockBox Created Push Notification :EventId $eventID ")
                navController.navigate(R.id.nav_invitation)
                clearNotification()
            }
        }

    }

    private fun clearNotification() {
        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        nMgr?.cancelAll()
    }


    private fun permissionShowHide(value: Int) {
        binding.llCarePoint.visibility = value
        binding.llLockBox.visibility = value
        binding.llMedList.visibility = value
        binding.llResources.visibility = value
        binding.llCareTeam.visibility = View.VISIBLE
        binding.llVitalStats.visibility = View.VISIBLE
        binding.llDiscussions.visibility = View.GONE
    }

    private fun setOnClickListeners() {
        binding.appBarDashboard.ivLovedOneProfile.setOnClickListener {
            navController.navigate(R.id.nav_loved_one)
        }
        binding.appBarDashboard.ivNotification.setOnClickListener {
//            showError(this, "Not Implemented")
            navController.navigate(R.id.nav_notifications)
        }
        cardViewUser.setOnClickListener {
            navController.navigate(R.id.nav_loved_one)
        }
    }


    override fun observeViewModel() {

        viewModel.userDetailByUUIDLiveData.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                when (it) {
                    is DataResult.Failure -> {
                        hideLoading()
                    }
                    is DataResult.Loading -> {
                        showLoading("")
                    }
                    is DataResult.Success -> {
                        hideLoading()
                        val userProfile = it.data.payload?.userProfiles
                        val lovedOneFirstName = userProfile?.firstname
                        val lovedOneLastName = userProfile?.lastname
                        var lovedOneFullName: String? = null
                        lovedOneFullName = if (lovedOneLastName.isNullOrEmpty()) {
                            lovedOneFirstName
                        } else {
                            "$lovedOneFirstName $lovedOneLastName"
                        }

                        val lovedOneProfilePic = userProfile?.profilePhoto
                        if (!lovedOneProfilePic.isNullOrEmpty())
                            Picasso.get().load(lovedOneProfilePic)
                                .placeholder(R.drawable.ic_defalut_profile_pic)
                                .into(imgLovedOne)

                        txtLoved.text = lovedOneFullName

                    }
                }
            }
        })


        // Observe Logout Response
        viewModel.logoutResponseLiveData.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                when (it) {
                    is DataResult.Failure -> {
                        hideLoading()
                        it.message?.let { showError(this, it.toString()) }
                    }
                    is DataResult.Loading -> {
                        showLoading("")
                    }
                    is DataResult.Success -> {
                        navigateToLoginScreen("home")
                    }
                }
            }
        })

        // Observe Get Home Data Api Response
        viewModel.homeResponseLiveData.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                when (it) {
                    is DataResult.Failure -> {
                        hideLoading()
                        showError(this, it.message.toString())
                    }
                    is DataResult.Loading -> {
                        showLoading("")
                    }
                    is DataResult.Success -> {
                        val payload = it.data.payload
                        // Set the notification icon
                        if (payload?.unreadNotificationsCount!! > 0) {
                            binding.appBarDashboard.ivNotification.setImageResource(R.drawable.ic_home_notification)
                        } else {
                            binding.appBarDashboard.ivNotification.setImageResource(R.drawable.ic_home_notification_inactive)
                        }

                        if (it.data.payload?.lovedOneUserProfile != null || it.data.payload?.lovedOneUserProfile != "") {
                            val lovedOneProfilePic = it.data.payload?.lovedOneUserProfile
                            if (!lovedOneProfilePic.isNullOrEmpty())
                                Picasso.get().load(lovedOneProfilePic)
                                    .placeholder(R.drawable.ic_defalut_profile_pic)
                                    .into(ivLovedOneProfile)
                        }
                        //set name of user name
                        txtLovedUserName.text = it.data.payload?.firstname
                        //save data
                        viewModel.saveLovedUser(it.data.payload!!.careTeamProfiles[0].loveUser)


                        // Get loved one user uuid
                        val lovedOneUUID = viewModel.getLovedOneUUID()
                        Log.d(TAG, "initHomeViews: lovedOneUUID :$lovedOneUUID")
                        // Get LoggedIn User uuid
                        val loggedInUserId = viewModel.getUUID()
                        Log.d(TAG, "initHomeViews: loggedInUserId :$loggedInUserId")
                        // find permission for loved one user
                        /* val permissions = payload?.careTeamProfiles?.filter {
                             (it.loveUserId == lovedOneUUID) && (it.userId == loggedInUserId)
                         }?.map {
                             it.permission
                         }?.first()*/

                        // Check if loggedIn User is the CareTeam Lead
                        val careTeam = payload.careTeamProfiles.filter {
                            it.userId == loggedInUserId
                        }
                        if (!careTeam.isNullOrEmpty()) {
                            if (careTeam[0].careRoles?.slug == CareRole.CareTeamLead.slug) {
                                viewModel.saveLoggedInUserCareTeamLead(true)
                            }
                        }


                        // find permission for loved one user
                        permissions = if (viewModel.isLoggedInUserLovedOne() == true) {
                            payload?.careTeamProfiles?.filter {
                                it.loveUserId == viewModel.getLovedOneUUID()
                            }?.map {
                                it.permission
                            }?.first()
                        } else {
                            payload?.careTeamProfiles?.filter {
                                (it.loveUserId == lovedOneUUID) && (it.userId == loggedInUserId)
                            }?.map {
                                it.permission
                            }?.first()
                        }

                        Log.d(TAG, "initHomeViews: Permissions : $permissions")
                        // Save Permissions to SharedPref
                        permissions?.let { it1 -> viewModel.savePermissions(it1) }
                        val perList = permissions?.split(',')
                            ?.map { it.trim() }
                        permissionCards(View.GONE)
                        for (i in perList?.indices!!) {
                            checkPermission(perList[i])
                        }
                        hideLoading()
                    }
                }
            }
        })
    }


    override fun initViewBinding() {

    }

    private fun permissionCards(value: Int) {
        // Care Teams and VitalStats Module are always visible
        binding.llCareTeam.visibility = View.VISIBLE
        binding.llVitalStats.visibility = View.VISIBLE

        // Other Cards are shown according to the permission
        binding.llLockBox.visibility = value
        binding.llMedList.visibility = value
        binding.llResources.visibility = value
        binding.llCarePoint.visibility = value

        binding.llDiscussions.visibility = View.GONE

    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        navController = findNavController(R.id.nav_host_fragment_content_dashboard)

        // Get Logged In User's profile info
        setDrawerInfo()

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
                        cardViewUser.isVisible = false


                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_my_medlist -> {
                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.medlist_header)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false
                        cardViewUser.isVisible = true

                        imgClose.setOnClickListener {
                            cardViewUser.isVisible = false
                        }

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
                        cardViewUser.isVisible = true

                        imgClose.setOnClickListener {
                            cardViewUser.isVisible = false
                        }

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
                        cardViewUser.isVisible = false

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
                        cardViewUser.isVisible = true

                        imgClose.setOnClickListener {
                            cardViewUser.isVisible = false
                        }

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
                    cardViewUser.isVisible = true
                    imgClose.setOnClickListener {
                        cardViewUser.isVisible = false
                    }

                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.resources)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = false
                    }
                    lockUnlockDrawer(false)
                }
                R.id.nav_care_points -> {
                    cardViewUser.isVisible = true
                    imgClose.setOnClickListener {
                        cardViewUser.isVisible = false
                    }

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
                    cardViewUser.isVisible = true
                    imgClose.setOnClickListener {
                        cardViewUser.isVisible = false
                    }
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
                    cardViewUser.isVisible = true

                    binding.appBarDashboard.apply {
                        tvTitle.text = getString(R.string.careteam)
                        clTopWrapper.isVisible = true
                        clEndWrapper.isVisible = true
                        clHomeWrapper.isVisible = false

                        imgClose.setOnClickListener {
                            cardViewUser.isVisible = false
                        }

                        tvNew.apply {
//                            isVisible = viewModel.isLoggedInUserCareTeamLead() == true
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
                    cardViewUser.isVisible = false

                    binding.appBarDashboard.apply {
                        clTopWrapper.isVisible = false
                    }
                }
            }
        }

        // Handle Push Notification Data
        if (intent != null && intent.hasExtra("isNotification")) {
            navController = findNavController(R.id.nav_host_fragment_content_dashboard)
            checkNotificationAction(intent.getBundleExtra("detail"))
        }
    }

    fun setDrawerInfo() {
        val loggedInUser = Prefs.with(ShepherdApp.appContext)?.getObject(
            Const.USER_DETAILS,
            UserProfiles::class.java
        )

        val firstName = loggedInUser?.firstname
        val lastName = loggedInUser?.lastname
        // Check if last name is null
        val fullName = if (lastName.isNullOrEmpty()) {
            "$firstName"
        } else {
            "$firstName $lastName"
        }

        val profilePicLoggedInUser = loggedInUser?.profilePhoto

        binding.ivName.text = fullName

        // Check if LovedOne is the loggedIn user
        if (viewModel.isLoggedInUserLovedOne() == true) {
            val lovedOnePic = viewModel.getLovedOnePic()
            if (lovedOnePic != null && lovedOnePic != "") {
                Picasso.get().load(lovedOnePic)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(binding.ivLoggedInUserProfile)
            } else {
                if (profilePicLoggedInUser != null && profilePicLoggedInUser != "") {
                    Picasso.get().load(profilePicLoggedInUser)
                        .placeholder(R.drawable.ic_defalut_profile_pic)
                        .into(binding.ivLoggedInUserProfile)
                }
            }
        } else {
            if (profilePicLoggedInUser != null && profilePicLoggedInUser != "") {
                Picasso.get().load(profilePicLoggedInUser)
                    .placeholder(R.drawable.ic_defalut_profile_pic)
                    .into(binding.ivLoggedInUserProfile)
            }
        }

        /* if (profilePicLoggedInUser != null && profilePicLoggedInUser != "") {
             Picasso.get().load(profilePicLoggedInUser)
                 .placeholder(R.drawable.ic_defalut_profile_pic)
                 .into(binding.ivLoggedInUserProfile)
         }*/

        // Set User's Role
        val role = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_ROLE, "")
        if (role.isNullOrEmpty()) {
            binding.tvRole.text = getString(R.string.care_team_leader)
        } else {
            binding.tvRole.text = role
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
//                showError(this, "Not implemented.")
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

    fun navigateToLoginScreen(source: String) {
        //startActivityWithFinish<LoginActivity>()
        showSuccess(this, "User logged out successfully")
//        viewModel.clearFirebaseToken()
        gotoLogin(source)
    }

    fun gotoLogin(source: String) {
        Prefs.with(ShepherdApp.appContext)?.removeAll()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("source", source)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)  // for open
    }

    override fun msgFromChildFragmentToActivity() {
        viewModel.getHomeData()
        viewModel.getUserDetailByUUID()
//        setDrawerInfo()

    }

    private
    var backPressed: Long = 0

    override fun onBackPressed() {
        // to check navigation fragments
        val navHostFragment =
            supportFragmentManager.primaryNavigationFragment as NavHostFragment?
        val fragmentManager: FragmentManager =
            navHostFragment!!.childFragmentManager
        when (fragmentManager.primaryNavigationFragment!!) {
            is CarePointsFragment, is MyMedListFragment, is LockBoxFragment, is CareTeamMembersFragment,
            is ResourcesFragment, is VitalStatsFragment, is MessagesFragment, is ProfileFragment -> {
                findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                    R.id.nav_dashboard
                )
                overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            is SettingFragment -> {
                findNavController(R.id.nav_host_fragment_content_dashboard).navigate(
                    R.id.nav_profile
                )
            }
            is DashboardFragment -> {
                if (backPressed + 2000 > System.currentTimeMillis()) {
                    // Finish this activity as well as all activities immediately below it in the current task that have the same affinity.
                    finishAffinity()
                    super.onBackPressed()
                } else Toast.makeText(
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

    private fun checkPermission(permission: String?) {
        when {
            Modules.CarePoints.value == permission -> {
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

    override fun onResume() {
        viewModel.getHomeData()
        viewModel.getUserDetailByUUID()
        super.onResume()
    }

    override fun updateViewVisibility(isVisible: Boolean) {
        // Update New Button Visibility
        if (isVisible) {
            binding.appBarDashboard.tvNew.visibility = View.VISIBLE
        } else {
            binding.appBarDashboard.tvNew.visibility = View.INVISIBLE
        }

    }

    /* override fun childSource(source: String) {
         val lovedOneDetail = viewModel.getLovedUserDetail()
         val isNewVisible = when (lovedOneDetail?.careRoles?.slug) {
             CareRole.CareTeamLead.slug -> {
                 true
             }
             else -> {
                 false
             }
         }
         when (source) {
             Const.Screen.CARE_POINT -> {
                 tvNew.isVisible = isNewVisible
             }
         }
     }*/
}