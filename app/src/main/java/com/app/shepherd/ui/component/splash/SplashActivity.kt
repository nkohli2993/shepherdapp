package com.app.shepherd.ui.component.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.SPLASH_DELAY
import com.app.shepherd.databinding.ActivitySplashBinding
import com.app.shepherd.ui.component.onBoarding.OnBoardingActivity
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
        navigateToOnBoardingScreen()
    }

    override fun observeViewModel() {
    }

    private fun navigateToOnBoardingScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<OnBoardingActivity>()
        }, SPLASH_DELAY.toLong())
    }
}
