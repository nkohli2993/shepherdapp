package com.app.shepherd.ui.component.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app.shepherd.SPLASH_DELAY
import com.app.shepherd.databinding.ActivitySplashBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.walk_through.WalkThroughActivity
import com.app.shepherd.ui.component.welcome.WelcomeUserActivity
import com.app.shepherd.utils.Const.DEVICE_ID
import com.app.shepherd.utils.Prefs
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
//        val token = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_TOKEN, "")
        // if token is empty ,then the user is logging first time
        /* if (token.isNullOrEmpty()) {
             navigateToOnBoardingScreen()
         } else {
             navigateToWelcomeUserScreen()
         }*/

        if (Prefs.with(this)!!.getString(DEVICE_ID, "").isNullOrEmpty()) {
            Prefs.with(this)!!.save(DEVICE_ID, CommonFunctions.getDeviceId(this))
        }
        navigateToOnBoardingScreen()

    }

    override fun observeViewModel() {
    }

    private fun navigateToOnBoardingScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<WalkThroughActivity>()
        }, SPLASH_DELAY.toLong())
    }

    private fun navigateToWelcomeUserScreen() {
        startActivity<WelcomeUserActivity>()
    }
}
