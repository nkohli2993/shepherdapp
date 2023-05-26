package com.shepherdapp.app.ui.component.subscription

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.google.gson.Gson
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionRequestModel
import com.shepherdapp.app.databinding.ActivitySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.ui.component.subscription.adapter.SubscriptionAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.getCurrentDate
import com.shepherdapp.app.utils.extensions.getDateAfter30Days
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_vitals.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * Created by Deepak Rattan on 29/09/22
 */
@AndroidEntryPoint
class SubscriptionActivity : BaseActivity(), View.OnClickListener {
    private var alertDialog: AlertDialog? = null
    private lateinit var binding: ActivitySubscriptionBinding
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()
    private var billingClient: BillingClient? = null
    private var purchasesUpdatedListener: PurchasesUpdatedListener? = null
    private var productDetailsList: ArrayList<ProductDetails?>? = ArrayList()
    var handler: Handler? = null
    private var planName: String? = null
    private var productID: String? = null
    private var nameOfPlan: String? = null
    private var amount: Double? = null
    private var orderID: String? = null
    private var expiryDate: String? = null
    private var source: String? = "SubscriptionActivity"
    private var isFreeTrial: Boolean = false
    private var isItemAlreadyPurchased: Boolean = false
    private var verifyingPurchase: Boolean = false


    private val TAG = "SubscriptionActivity"

    override fun initViewBinding() {
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.getStringExtra("source") != null) {
            source = intent.getStringExtra("source")
        }

        binding.listener = this
        showLoading("")

        purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            // Google Play delivers the result of the purchase operation here
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        Log.d(TAG, "verifySubPurchase: " + "93")
                        verifySubPurchase(purchase)
                    }
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Log.d(
                    TAG,
                    "PurchasesUpdatedListener: USER_CANCELED  " + billingResult.responseCode + "   " + billingResult.debugMessage
                )
            } else {
                // Handle any other error codes.
                Log.d(
                    TAG,
                    "PurchasesUpdatedListener: " + billingResult.responseCode + "   " + billingResult.debugMessage
                )
            }
        }

        //Step 2. Initialize a BillingClient with PurchasesUpdatedListener onCreate method
        // BillingClient is the main interface for communication between the Google Play Billing Library and the rest of the app
        // BillingClient provides convenience methods, both synchronous and asynchronous, for many common billing operations.
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener!!)
            .build()

        // start the connection after initializing the billing client
        establishConnection()
    }

    override fun onResume() {
        super.onResume()
        // Step 7: Handling pending transactions
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                        Log.d(TAG, "verifySubPurchase: " + "554")
                        verifySubPurchase(purchase)
                    }

                }
            }
        }
    }


    //Step 3. Establish a connection to Google Play
    fun establishConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    // Step 4: Show products available to buy
    fun showProducts() {
        val productList = ImmutableList.of(
            //Product 1
            Product.newBuilder()
                .setProductId("monthly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            Product.newBuilder()
                .setProductId("yearly")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            /*  Product.newBuilder()
                  .setProductId("one_month")
                  .setProductType(BillingClient.ProductType.SUBS)
                  .build(),
              Product.newBuilder()
                  .setProductId("one_year")
                  .setProductType(BillingClient.ProductType.SUBS)
                  .build()*/
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()


        billingClient?.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
            Log.d(TAG, "showProducts: prodDetailsList:${prodDetailsList}")
            productDetailsList?.clear()
            Handler(Looper.getMainLooper()).postDelayed({
                productDetailsList?.addAll(prodDetailsList)
                setAdapter()
            }, 2000)
        }
    }

    // Step 5 : Launch the purchase flow
    private fun launchPurchaseFlow(productDetails: ProductDetails) {
        assert(productDetails.subscriptionOfferDetails != null)
        val productDetailsParamsList = ImmutableList.of(
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(productDetails.subscriptionOfferDetails!![0].offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient!!.launchBillingFlow(this, billingFlowParams)
    }

    // Step 6: Processing purchases / Verify Payment
    private fun verifySubPurchase(purchases: Purchase) {
        verifyingPurchase = true
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()

        billingClient?.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                orderID = purchases.orderId
                when (planName) {
                    Const.SubscriptionPlan.Yearly -> {
                        nameOfPlan = "Yearly"
                    }

                    Const.SubscriptionPlan.Monthly -> {
                        nameOfPlan = "Monthly"
                    }
                }
                expiryDate = planName?.let { getDate(it) }

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "transactionId : $orderID")
                    Log.d(TAG, "plan : $nameOfPlan")
                    Log.d(TAG, "amount : $amount")
                    Log.d(TAG, "expiryDate : $expiryDate")
                    Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
                    Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
                    Log.d(TAG, "Package Name: " + purchases.packageName)
                    Log.d(TAG, "Purchase OrderID: " + purchases.orderId)
                    Log.d(TAG, "Product ID: $productID")
                    Log.d(TAG, "Plan Name : $planName")
                    Log.d(TAG, "verifySubPurchase: " + "240")
                }


                // A successful purchase generates a purchase token, which is a unique identifier that
                // represents the user and the product ID for the in-app product they purchased.

                //Validate Subscription
                subscriptionViewModel.validateSubscription(
                    ValidateSubscriptionRequestModel(
                        purchaseToken = purchases.purchaseToken,
                        packageName = purchases.packageName,
                        productId = productID,
                        transactionId = orderID
                    )
                )
            } else {
                Log.d(
                    TAG,
                    "verifySubPurchase: " + "253" + billingResult.responseCode + "    " + billingResult.debugMessage
                )
            }
        }
    }

    private fun setAdapter() {
        hideLoading()
        subscriptionAdapter =
            SubscriptionAdapter(this, subscriptionViewModel, productDetailsList)
        binding.viewPagerSubscription.adapter = subscriptionAdapter
//        binding.viewPagerSubscription.setCurrentItem((productDetailsList?.get(0) ?: 0) as Int, false)

        //For multiple page
        binding.viewPagerSubscription.clipToPadding = false
        binding.viewPagerSubscription.clipChildren = false
        binding.viewPagerSubscription.offscreenPageLimit = 3
        binding.viewPagerSubscription.getChildAt(0).overScrollMode =
            OVER_SCROLL_NEVER

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

        // Observe the response of Create Subscription
        subscriptionViewModel.createSubscriptionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                }

                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {
                    hideLoading()
                    showSuccess(this, it.data.message.toString())

                    // Save Subscription Status
                    subscriptionViewModel.saveSubscriptionPurchased(isSubscriptionPurchased = true)

                    val currentUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
                        Const.USER_DETAILS,
                        UserProfile::class.java
                    )
                    val firstName = currentUser?.firstname
                    val lastName = currentUser?.lastname
                    val fullName = if (lastName.isNullOrEmpty()) {
                        firstName
                    } else {
                        "$firstName $lastName"
                    }

                    val builder = AlertDialog.Builder(this)
                    if (alertDialog == null) {
                        alertDialog = builder.apply {
                            setTitle("Hi, $fullName")
                            setCancelable(false)
                            setMessage("You have successfully bought your $nameOfPlan plan")
                            setPositiveButton("OK") { _, _ ->
                                if (source == "My Subscription") {
                                    onBackPressedDispatcher.onBackPressed()
                                } else {
                                    if (source == "Login Screen") {
                                        navigateToHomeScreen()
                                    } else {
                                        val intent =
                                            Intent(
                                                this@SubscriptionActivity,
                                                AddLovedOneActivity::class.java
                                            )
                                        intent.putExtra("source", source)
                                        startActivity(intent)
                                        overridePendingTransition(
                                            R.anim.slide_in_right,
                                            R.anim.slide_out_left
                                        )
                                    }

                                }
                            }
                        }.create()
                        alertDialog?.show()
                        alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
                            ?.setTextColor(Color.BLACK)
                    }
                }
            }
        }

        // Observe the response of Validate Subscription
        subscriptionViewModel.validateSubscriptionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    showError(this, it.message.toString())
                }

                is DataResult.Loading -> {
                    showLoading("")
                }

                is DataResult.Success -> {


                    if (isItemAlreadyPurchased) {
                        hideLoading()
                        showSuccess(this, "Subscription Validated Successfully")
                        // Redirect to AddLovedOneActivity
                        if (source == "Login Screen") {
                            navigateToHomeScreen()
                        } else {
                            val intent =
                                Intent(
                                    this@SubscriptionActivity,
                                    AddLovedOneActivity::class.java
                                )
                            intent.putExtra("source", source)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                        }

                    } else {
                        expiryDate = planName?.let { getDate(it) }
                        // Create Subscription
                        if (isFreeTrial) {
                            subscriptionViewModel.createSubscription(
                                SubscriptionRequestModel(
                                    transactionId = orderID,
                                    plan = nameOfPlan,
                                    amount = amount,
                                    expiryDate = expiryDate,
                                    trialStartAt = getCurrentDate(),
                                    trialEndAt = getDateAfter30Days()
                                )
                            )
                        } else {
                            subscriptionViewModel.createSubscription(
                                SubscriptionRequestModel(
                                    transactionId = orderID,
                                    plan = nameOfPlan,
                                    amount = amount,
                                    expiryDate = expiryDate,
                                    trialStartAt = null,
                                    trialEndAt = null,
                                )
                            )
                        }
                    }
                }
            }
        }

    }

    // Navigate to Home Screen with loved one array
    private fun navigateToHomeScreen() {
        Prefs.with(this)?.save(Const.ON_BOARD, true)
        Prefs.with(ShepherdApp.appContext)?.save(Const.USER_INACTIVE_LOGOUT, false)
        val intent = Intent(this, HomeActivity::class.java)
//        intent.putExtra(Const.LOVED_ONE_ARRAY, userLovedOneArrayList)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }


    private fun openSubscriptionPlan(singleEvent: SingleEvent<ProductDetails?>) {
        singleEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "openSubscriptionPlan: Clicked Plan is $it ")
            planName = it.name
            productID = it.productId
            Log.d(TAG, "openSubscriptionPlan: Plan Name : ${it.name}")
            Log.d(TAG, "openSubscriptionPlan: Plan id : $productID")
            /*  amount = it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                  0
              )?.formattedPrice*/

            if (it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                    0
                )?.formattedPrice == "Free"
            ) {
                // Free Trial
                val priceMicros =
                    it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(1)?.priceAmountMicros
                // 1,000,000 micro-units equal one unit of the currency
                val price = priceMicros?.div(1000000)
                amount = price?.toDouble()

                Log.d(TAG, "priceMicros : $priceMicros")
                Log.d(TAG, "amount : $amount")

                isFreeTrial = true

            } else {
                val priceMicros =
                    it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.priceAmountMicros
                // 1,000,000 micro-units equal one unit of the currency
                val price = priceMicros?.div(1000000)
                amount = price?.toDouble()
                Log.d(TAG, "priceMicros : $priceMicros")
                Log.d(TAG, "amount : $amount")

                isFreeTrial = false
            }

            /*val priceMicros =
                it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.priceAmountMicros*/
            // 1,000,000 micro-units equal one unit of the currency
//            val price = priceMicros?.div(1000000)
//            amount = price?.toDouble()
//            Log.d(TAG, "priceMicros : $priceMicros")
//            Log.d(TAG, "amount : $amount")

            // Check if product is already purchased or not
            checkIfProductAlreadyPurchased(it)


//            launchPurchaseFlow(it)

        }
    }

    private fun checkIfProductAlreadyPurchased(clickedProduct: ProductDetails) {
        billingClient?.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult1: BillingResult, list: List<Purchase> ->

            if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "List Size :${list.size} ")

                // We will get active subscriptions if list id not empty
                if (list.isNotEmpty()) {
                    for (purchase in list) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged) {
                            val originalJson = purchase.originalJson
                            Log.d(TAG, "json : $originalJson")
                            // Parse Json
                            var map: Map<String, Any> = HashMap()
                            map = Gson().fromJson(originalJson, map.javaClass)

                            Log.d(TAG, "onBillingSetupFinished: map is $map")
                            val productId = map["productId"] as String?
                            Log.d(TAG, "onBillingSetupFinished: productId : $productId")
                            val orderId = map["orderId"] as String?
                            Log.d(TAG, "onBillingSetupFinished: orderId : $orderId")

                            val packageName = map["packageName"] as String?
                            Log.d(TAG, "onBillingSetupFinished: packageName : $packageName")

                            val purchaseToken = map["purchaseToken"] as String?
                            Log.d(TAG, "onBillingSetupFinished: purchaseToken : $purchaseToken")

                            orderID = orderId
                            nameOfPlan = clickedProduct.name
                            // If product ID of clicked item matches with the id of subscription item
                            if (clickedProduct.productId == productId) {
                                Log.d(
                                    TAG,
                                    "checkIfProductAlreadyPurchased: Subscription already purchased"
                                )
                                isItemAlreadyPurchased = false
                                if (subscriptionViewModel.isUserAttachedToEnterprise() == true) {
                                    isItemAlreadyPurchased = true
                                }
                                //Validate Subscription
                                subscriptionViewModel.validateSubscription(
                                    ValidateSubscriptionRequestModel(
                                        purchaseToken = purchaseToken,
                                        packageName = packageName,
                                        productId = productId,
                                        transactionId = orderId
                                    )
                                )
                            } else {
                                Log.d(
                                    TAG,
                                    "checkIfProductAlreadyPurchased: productId doesn't match with the id of subscribed item, means clicked item not subscribed"
                                )
                                isItemAlreadyPurchased = false
                                launchPurchaseFlow(clickedProduct)
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "checkIfProductAlreadyPurchased: No active subscription found")
                    launchPurchaseFlow(clickedProduct)
                }
            } else {
                Log.d(TAG, "checkIfProductAlreadyPurchased: No active subscription found")

            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun getDate(planName: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
        val currentCal: Calendar = Calendar.getInstance()
        val currentDate: String = dateFormat.format(currentCal.time)
        Log.d(TAG, "current date : $currentDate")
        when (planName) {
            Const.SubscriptionPlan.ONE_WEEK -> {
                currentCal.add(Calendar.DATE, 7)
            }

            Const.SubscriptionPlan.ONE_MONTH -> {
                currentCal.add(Calendar.MONTH, 1)
            }

            Const.SubscriptionPlan.ONE_YEAR -> {
                currentCal.add(Calendar.YEAR, 1)
            }
        }

        val toDate: String = dateFormat.format(currentCal.time)
        Log.d(TAG, "expiry date for $planName plan  : $toDate")
        return toDate
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

}