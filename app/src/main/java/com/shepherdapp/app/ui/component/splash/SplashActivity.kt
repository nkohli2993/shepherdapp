package com.shepherdapp.app.ui.component.splash

import CommonFunctions
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.android.billingclient.api.*
import com.google.firebase.messaging.FirebaseMessaging
import com.shepherdapp.app.SPLASH_DELAY
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.databinding.ActivitySplashBinding
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.addLovedOneCondition.AddLovedOneConditionActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.login.LoginActivity
import com.shepherdapp.app.ui.component.subscription.SubscriptionActivity
import com.shepherdapp.app.ui.component.walk_through.WalkThroughActivity
import com.shepherdapp.app.ui.component.welcome.WelcomeUserActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Const.DEVICE_ID
import com.shepherdapp.app.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Sumit Kumar
 */
@AndroidEntryPoint
class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding
    private var TAG = "SplashActivity"
    private var firebaseToken: String? = null
    var handler: Handler? = null
    var billingClient: BillingClient? = null


    override fun initViewBinding() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check if user is already loggedIn
        val token = Prefs.with(ShepherdApp.appContext)!!.getString(Const.USER_TOKEN, "")

        // Device Id
        if (Prefs.with(this)?.getString(DEVICE_ID, "").isNullOrEmpty()) {
            Prefs.with(this)?.save(DEVICE_ID, CommonFunctions.getDeviceId(this))
        }

        // Firebase Token
        if (Prefs.with(this)?.getString(Const.FIREBASE_TOKEN, "").isNullOrEmpty()) {
            generateFirebaseToken()
        }

        if (Prefs.with(this)?.getBoolean(Const.ON_BOARD) == false) {
            navigateToOnBoardingScreen()
        } else {
            navigateToLoginScreen()
        }
//        navigateToSubscriptionScreen()


    }

    override fun observeViewModel() {
    }


    private fun generateFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", it.exception)
                return@addOnCompleteListener
            }
            firebaseToken = it.result
            // Get new FCM registration token
            Prefs.with(this)!!.save(Const.FIREBASE_TOKEN, it.result)
            // Log and toast
            Log.d(TAG, "Firebase token generated: ${it.result}")
        }
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

    private fun navigateToSubscriptionScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivityWithFinish<SubscriptionActivity>()
        }, SPLASH_DELAY.toLong())
    }

    fun checkSubscription() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
            .build()
        val finalBillingClient: BillingClient = billingClient as BillingClient
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()
                    ) { billingResult1: BillingResult, list: List<Purchase> ->
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d("testOffer", list.size.toString() + " size")
                            if (list.size > 0) {
//                                prefs.setPremium(1) // set 1 to activate premium feature
                                var i = 0
                                for (purchase in list) {
                                    //Here you can manage each product, if you have multiple subscription
                                    Log.d(
                                        "testOffer",
                                        purchase.originalJson
                                    ) // Get to see the order information
                                    Log.d("testOffer", " index$i")
                                    i++
                                }
                            } else {
//                                prefs.setPremium(0) // set 0 to de-activate premium feature
                            }
                        }
                    }
                }
            }
        })
    }


}
