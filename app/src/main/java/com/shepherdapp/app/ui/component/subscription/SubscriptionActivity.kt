package com.shepherdapp.app.ui.component.subscription

import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.shepherdapp.app.data.dto.subscription.SubscriptionModel
import com.shepherdapp.app.databinding.ActivitySubscriptionBinding
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.subscription.adapter.SubscriptionAdapter
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

/**
 * Created by Deepak Rattan on 29/09/22
 */
@AndroidEntryPoint
class SubscriptionActivity : BaseActivity() {
    private lateinit var binding: ActivitySubscriptionBinding
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()

    private val TAG = "SubscriptionActivity"

    override fun initViewBinding() {
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        val adapter = SubscriptionAdapter(subscriptionViewModel, getSubscriptionItems())
        binding.viewPagerSubscription.adapter = adapter


        //For multiple page
        binding.viewPagerSubscription.clipToPadding = false
        binding.viewPagerSubscription.clipChildren = false
        binding.viewPagerSubscription.offscreenPageLimit = 3
        binding.viewPagerSubscription.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositeTransformer = CompositePageTransformer()
        compositeTransformer.addTransformer(MarginPageTransformer(40))
        compositeTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.95f + r * 0.05f)
        }
        binding.viewPagerSubscription.setPageTransformer(compositeTransformer)

    }


    override fun observeViewModel() {
        // Observe open subscription plan live data
        observe(subscriptionViewModel.openSubscriptionPlanLiveData, ::openSubscriptionPlan)

    }

    private fun openSubscriptionPlan(singleEvent: SingleEvent<SubscriptionModel>) {
        singleEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "openSubscriptionPlan: Clicked Plan is $it ")
        }

    }

    // Get Subscription Items
    private fun getSubscriptionItems(): ArrayList<SubscriptionModel> {
        val subscriptionItems = ArrayList<SubscriptionModel>()
        subscriptionItems.add(
            SubscriptionModel(
                planName = "Basic",
                planDesc = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.",
                lovedOneCount = "Max 1 loved one can added",
                planPrice = "50"
            )
        )

        subscriptionItems.add(
            SubscriptionModel(
                planName = "Premium",
                planDesc = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.",
                lovedOneCount = "Max 10 loved one can added",
                planPrice = "500"
            )
        )

        subscriptionItems.add(
            SubscriptionModel(
                planName = "Basic",
                planDesc = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout.",
                lovedOneCount = "Max 1 loved one can added",
                planPrice = "50"
            )
        )

        return subscriptionItems
    }

}