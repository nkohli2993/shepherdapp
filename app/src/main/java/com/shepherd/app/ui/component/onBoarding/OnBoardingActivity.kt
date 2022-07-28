package com.shepherd.app.ui.component.onBoarding

import android.os.Bundle
import android.view.View
import com.shepherd.app.R
import com.shepherd.app.ui.base.BaseActivity
import com.shepherd.app.ui.component.login.LoginActivity
import com.shepherd.app.databinding.ActivityOnboardingBinding
import com.shepherd.app.ui.component.createAccount.CreateAccountActivity
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
