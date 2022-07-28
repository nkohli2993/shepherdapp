package com.shepherd.app.ui.component.splash

import CommonFunctions
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.shepherd.app.SPLASH_DELAY
import com.shepherd.app.ShepherdApp
import com.shepherd.app.databinding.ActivitySplashBinding
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherd.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherd.app.ui.component.home.HomeActivity
import com.shepherd.app.ui.component.login.LoginActivity
import com.shepherd.app.ui.component.walk_through.WalkThroughActivity
import com.shepherd.app.ui.component.welcome.WelcomeUserActivity
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Const.DEVICE_ID
import com.shepherd.app.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check if user is already loggedIn
        val token = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_TOKEN, "")

        if (Prefs.with(this)?.getString(DEVICE_ID, "").isNullOrEmpty()) {
            Prefs.with(this)?.save(DEVICE_ID, CommonFunctions.getDeviceId(this))
        }

        if (token.isNullOrEmpty()) {
            navigateToOnBoardingScreen()
        } else {
            navigateToLoginScreen()
        }
    }

    override fun observeViewModel() {
    }

    private fun navigateToOnBoardingScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<WalkThroughActivity>()
        }, SPLASH_DELAY.toLong())
    }

    private fun navigateToLoginScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<LoginActivity>()
        }, SPLASH_DELAY.toLong())
    }

    private fun navigateToWelcomeUserScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<WelcomeUserActivity>()
        }, SPLASH_DELAY.toLong())
    }

    private fun navigateToMedicalConditionActivity() {
        startActivity<AddLovedOneConditionActivity>()
    }

    private fun navigateToLovedOneActivity() {
        startActivity<AddLovedOneActivity>()
    }

    private fun navigateToHomeScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<HomeActivity>()
        }, SPLASH_DELAY.toLong())
    }
}
