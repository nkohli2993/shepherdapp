package com.app.shepherd.ui.component.onBoarding

import android.os.Bundle
import android.view.View
import com.app.shepherd.R
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.login.LoginActivity
import com.app.shepherd.databinding.ActivityOnboardingBinding
import com.app.shepherd.ui.component.addLovedOne.AddLovedOneActivity
import com.app.shepherd.ui.component.onBoarding.adapter.OnBoardingImagesAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_onboarding.*

/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class OnBoardingActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOnboardingBinding
    private var mOnBoardingImagesAdapter: OnBoardingImagesAdapter? = null

    override fun initViewBinding() {
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this

        setImagesAdapter()
    }


    override fun observeViewModel() {
    }

    private fun setImagesAdapter() {
        val list: ArrayList<Int> = ArrayList()
        list.add(R.drawable.onboarding_01)
        list.add(R.drawable.onboarding_02)
        list.add(R.drawable.onboarding_03)
        list.add(R.drawable.onboarding_04)
        mOnBoardingImagesAdapter =
            OnBoardingImagesAdapter(this, list)
        viewPager.adapter = mOnBoardingImagesAdapter
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
        startActivity<AddLovedOneActivity>()
    }

    private fun navigateToLoginScreen() {
        startActivity<LoginActivity>()
    }

}
