package com.shepherdapp.app.ui.component.subscription

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.subscription.SubscriptionModel
import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.validate_subscription.ValidateSubscriptionRequestModel
import com.shepherdapp.app.databinding.ActivitySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.addLovedOne.AddLovedOneActivity
import com.shepherdapp.app.ui.component.subscription.adapter.SubscriptionAdapter
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.utils.observe
import com.shepherdapp.app.view_model.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


/**
 * Created by Deepak Rattan on 29/09/22
 */
@AndroidEntryPoint
class SubscriptionActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySubscriptionBinding
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private val subscriptionViewModel: SubscriptionViewModel by viewModels()
    private var billingClient: BillingClient? = null
    private var productDetailsList: ArrayList<ProductDetails?>? = ArrayList()
    var handler: Handler? = null
    private var planName: String? = null
    private var productID: String? = null
    private var nameOfPlan: String? = null
    private var amount: Double? = null
    private var orderID: String? = null
    private var expiryDate: String? = null


    private val TAG = "SubscriptionActivity"

    override fun initViewBinding() {
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.listener = this
        showLoading("")
        //Step 2. Initialize a BillingClient with PurchasesUpdatedListener onCreate method
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener { billingResult, list ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                    for (purchase in list) {
                        verifySubPurchase(purchase)
                    }
                }
            }.build()

        // start the connection after initializing the billing client
        establishConnection()


    }

    private fun setAdapter() {
        hideLoading()
        subscriptionAdapter = SubscriptionAdapter(this, subscriptionViewModel, productDetailsList)
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
        val productList = ImmutableList.of( //Product 1
            Product.newBuilder()
                .setProductId("one_week")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),  //Product 2
            Product.newBuilder()
                .setProductId("one_month")
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),  //Product 3
            Product.newBuilder()
                .setProductId("one_year")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()


        billingClient?.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
            Log.d(TAG, "showProducts: prodDetailsList:${prodDetailsList}")
            productDetailsList?.clear()
//            productDetailsList?.addAll(prodDetailsList)
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
        val acknowledgePurchaseParams = AcknowledgePurchaseParams
            .newBuilder()
            .setPurchaseToken(purchases.purchaseToken)
            .build()

        billingClient!!.acknowledgePurchase(
            acknowledgePurchaseParams
        ) { billingResult: BillingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchaseToken = purchases.purchaseToken
                orderID = purchases.orderId
//                var nameOfPlan: String? = null
                when (planName) {
                    Const.SubscriptionPlan.ONE_WEEK -> {
                        nameOfPlan = "Weekly"
//                        expiryDate =
                    }
                    Const.SubscriptionPlan.ONE_MONTH -> {
                        nameOfPlan = "Monthly"
                    }
                    Const.SubscriptionPlan.ONE_YEAR -> {
                        nameOfPlan = "Yearly"
                    }
                }
                expiryDate = planName?.let { getDate(it) }

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

                //Validate Subscription
                subscriptionViewModel.validateSubscription(
                    ValidateSubscriptionRequestModel(
                        purchaseToken = purchases.purchaseToken,
                        packageName = purchases.packageName,
                        productId = productID

                    )
                )

                /*  subscriptionViewModel.createSubscription(
                      SubscriptionRequestModel(
                          transactionId = orderID,
                          plan = nameOfPlan,
                          amount = amount,
                          expiryDate = expiryDate
                      )
                  )*/


            }
        }
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
                    val dialog = builder.apply {
                        setTitle("Hi, $fullName")
                        setMessage("You have successfully bought your $nameOfPlan plan")
                        setPositiveButton("OK") { _, _ ->
                            startActivity<AddLovedOneActivity>()
                        }
                    }.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)

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
                    hideLoading()
                    showSuccess(this, "Subscription Validated Successfully")

                    // Create Subscription
                    subscriptionViewModel.createSubscription(
                        SubscriptionRequestModel(
                            transactionId = orderID,
                            plan = nameOfPlan,
                            amount = amount,
                            expiryDate = expiryDate
                        )
                    )
                }
            }
        }

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

            val priceMicros =
                it.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(0)?.priceAmountMicros
            // 1,000,000 micro-units equal one unit of the currency
            val price = priceMicros?.div(1000000)
            amount = price?.toDouble()
            Log.d(TAG, "priceMicros : $priceMicros")
            Log.d(TAG, "amount : $amount")

            launchPurchaseFlow(it)

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

    override fun onResume() {
        super.onResume()
        // Step 7: Handling pending transactions
        billingClient!!.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        ) { billingResult: BillingResult, list: List<Purchase> ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in list) {
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                        verifySubPurchase(purchase)
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getDate(planName: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
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