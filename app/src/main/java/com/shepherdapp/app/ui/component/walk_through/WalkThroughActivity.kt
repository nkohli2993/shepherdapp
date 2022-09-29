package com.shepherdapp.app.ui.component.walk_through

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.ActivityWalkThroughBinding
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.onBoarding.adapter.OnBoardingImagesAdapter
import com.shepherdapp.app.ui.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_onboarding.*


@AndroidEntryPoint
class WalkThroughActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityWalkThroughBinding
    private var mOnBoardingImagesAdapter: OnBoardingImagesAdapter? = null
    private var selectedPage: Int = 0


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
                R.drawable.walkthrough_1,
                "Care for your loved ones.\nCare for yourself.",
                "Welcome. Shepherd was designed for the health and well-being of family caregivers and those for whom they are giving care."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walkthrough_2,
                "Manage the stress that \ncomes with caregiving.",
                "Whether you are managing the care of a parent, a husband or wife, or a special- needs child, Shepherd can help you reduce the stress that comes with caregiving."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walkthrough_3,
                "Take control of your day.",
                "Shepherd gives you more control over your daily workload, and includes expert advice to help guide you through the caregiving experience."
            )
        )
        /* list.add(
             WalkThroughModel(
                 R.drawable.walkthrough_4,
                 "Try it for free.",
                 "Try Shepherd for 30 days and see how much better things can be."
             )
         )*/
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
                selectedPage = position
                binding.btnSkip.isVisible = position != 2
                if ((position == 0 || position == 1)) {
                    binding.btnGetStarted.visibility = View.GONE
                    binding.btnNext.visibility = View.VISIBLE
                } else {
                    binding.btnGetStarted.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.GONE
                }
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
            R.id.btnNext -> {
                // Move to next slide of view pager
                binding.viewPager.currentItem = selectedPage + 1
            }
        }
    }

    private fun navigateToWelcomeScreen() {
        startActivityWithFinish<WelcomeActivity>()
    }
}