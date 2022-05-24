package com.app.shepherd.ui.component.walkThrough

import com.app.shepherd.ui.base.BaseActivity
import android.os.Bundle
import android.view.View
import com.app.shepherd.R
import com.app.shepherd.databinding.ActivityWalkThroughBinding
import com.app.shepherd.ui.component.onBoarding.adapter.OnBoardingImagesAdapter
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
        val list: ArrayList<WalkThroughModel> = ArrayList()
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Care for your loved ones, Care for yourself",
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Care for your loved ones, Care for yourself",
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Care for your loved ones, Care for yourself",
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            )
        )
        list.add(
            WalkThroughModel(
                R.drawable.walk_through_1,
                "Care for your loved ones, Care for yourself",
                "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout."
            )
        )
        mOnBoardingImagesAdapter =
            OnBoardingImagesAdapter(this, list)
        viewPager.adapter = mOnBoardingImagesAdapter
        binding.viewPagerIndicators.setupWithViewPager(viewPager)
    }

    override fun observeViewModel() {
    }

    override fun onClick(v: View?) {
    }
}