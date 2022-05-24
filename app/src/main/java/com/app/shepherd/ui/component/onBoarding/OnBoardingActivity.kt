package com.app.shepherd.ui.component.onBoarding

import android.os.Bundle
import android.view.View
import com.app.shepherd.R
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.databinding.ActivityOnboardingBinding
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.createAccount.CreateAccountActivity
import com.app.shepherd.ui.component.onBoarding.adapter.OnBoardingImagesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_onboarding.*

/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class OnBoardingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOnboardingBinding

    override fun initViewBinding() {
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this
    }


    override fun observeViewModel() {
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.textViewLogin -> {
                navigateToLoginScreen()
            }
            R.id.buttonCreateAccount -> {
                navigateToCreateAccountScreen()
            }
        }
    }

    private fun navigateToCreateAccountScreen() {
        startActivity<CreateAccountActivity>()
    }

    private fun navigateToLoginScreen() {
        startActivity<LoginActivity>()
    }

}
