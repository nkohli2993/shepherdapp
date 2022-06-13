package com.app.shepherd.ui.component.walkThrough

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityWalkThroughBinding
import com.app.shepherd.ui.base.BaseActivity
import com.app.shepherd.ui.component.onBoarding.adapter.OnBoardingImagesAdapter
import com.app.shepherd.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_onboarding.*


@AndroidEntryPoint
class WalkThroughActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalkThroughBinding
    private var mOnBoardingImagesAdapter: OnBoardingImagesAdapter? = null


    override fun initViewBinding() {
        binding = ActivityWalkThroughBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.listener = this

        setImagesAdapter()
    }

    private fun setImagesAdapter() {
        binding.viewPagerIndicators.setSliderWidth(50f)
        binding.viewPagerIndicators.setSliderHeight(10f)
        val list: ArrayList<WalkThroughModel> = ArrayList()
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Care for your loved ones.\nCare for yourself.",
                "Welcome. Shepherd was designed for the health and well-being of family caregivers and those for whom they are giving care."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Manage the stress that \ncomes with caregiving.",
                "Whether you are managing the care of a parent, a husband or wife, or a special- needs child, Shepherd can help you reduce the stress that comes with caregiving."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Take control of your day.",
                "Shepherd gives you more control over your daily workload, and includes expert advice to help guide you through the caregiving experience."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Try it for free.",
                "Try Shepherd for 30 days and see how much better things can be."
            )
        )
        mOnBoardingImagesAdapter =
            OnBoardingImagesAdapter(this, list)
        viewPager.adapter = mOnBoardingImagesAdapter
        binding.viewPagerIndicators.setupWithViewPager(viewPager)
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.btnSkip.isVisible = position != 3
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun observeViewModel() {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSkip -> {
                navigateToWelcomeScreen()
            }
            R.id.btnGetStarted -> {
                navigateToWelcomeScreen()
            }
        }
    }

    private fun navigateToWelcomeScreen() {
        startActivity<WelcomeActivity>()
    }
}