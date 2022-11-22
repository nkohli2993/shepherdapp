package com.shepherdapp.app.ui.component.subscription

import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import com.shepherdapp.app.data.dto.subscription.SubscriptionModel
import com.shepherdapp.app.databinding.ActivitySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseActivity
import com.shepherdapp.app.ui.component.subscription.adapter.SubscriptionAdapter
import com.shepherdapp.app.utils.SingleEvent
import com.shepherdapp.app.utils.extensions.showSuccess
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
    private var billingClient: BillingClient? = null
    private var productDetailsList: ArrayList<ProductDetails?>? = ArrayList()
    var handler: Handler? = null


    private val TAG = "SubscriptionActivity"

    override fun initViewBinding() {
        binding = ActivitySubscriptionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        setAdapter()

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
        subscriptionAdapter = SubscriptionAdapter(subscriptionViewModel, productDetailsList)
        binding.viewPagerSubscription.adapter = subscriptionAdapter

        /* //For multiple page
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
         binding.viewPagerSubscription.setPageTransformer(compositeTransformer)*/

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
            productDetailsList?.addAll(prodDetailsList)
//            subscriptionAdapter?.addData(productDetailsList)


            subscriptionAdapter = SubscriptionAdapter(subscriptionViewModel, productDetailsList)
            subscriptionAdapter?.notifyDataSetChanged()
            binding.viewPagerSubscription.adapter = subscriptionAdapter
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
    }

    /* @SuppressLint("SetTextI18n")
     fun showProducts() {
         val productList: ImmutableList<Product> = ImmutableList.of(
             //Product 1
             Product.newBuilder()
                 .setProductId("one_week")
                 .setProductType(BillingClient.ProductType.SUBS)
                 .build(),
             //Product 2
             Product.newBuilder()
                 .setProductId("one_month")
                 .setProductType(BillingClient.ProductType.SUBS)
                 .build(),
             //Product 3
             Product.newBuilder()
                 .setProductId("one_year")
                 .setProductType(BillingClient.ProductType.SUBS)
                 .build()
         )
         val params = QueryProductDetailsParams.newBuilder()
             .setProductList(productList)
             .build()

         billingClient!!.queryProductDetailsAsync(
             params,
         ) { billingResult: BillingResult?, prodDetailsList: List<ProductDetails?>? ->

             Log.d(TAG, "showProducts: prodDetailsList:${prodDetailsList}")
             // Process the result
             productDetailsList?.clear()
             handler?.postDelayed(Runnable {
 //                loadProducts.setVisibility(View.INVISIBLE)
                 if (prodDetailsList != null) {
                     productDetailsList?.addAll(prodDetailsList)
                 }
                 Log.d(
                     TAG,
                     productDetailsList?.size.toString() + " number of products"
                 )
                 *//*adapter = ProductDetailsAdapter(
                    applicationContext,
                    productDetailsList,
                    this@Subscriptions
                )*//*
//                recyclerView.setHasFixedSize(true)
                *//* recyclerView.setLayoutManager(
                     LinearLayoutManager(
                         this@Subscriptions,
                         LinearLayoutManager.VERTICAL,
                         false
                     )
                 )*//*
//                recyclerView.setAdapter(adapter)
                val adapter = SubscriptionAdapter(subscriptionViewModel, prodDetailsList)
                binding.viewPagerSubscription.adapter = adapter


                //For multiple page
                binding.viewPagerSubscription.clipToPadding = false
                binding.viewPagerSubscription.clipChildren = false
                binding.viewPagerSubscription.offscreenPageLimit = 3
                binding.viewPagerSubscription.getChildAt(0).overScrollMode =
                    RecyclerView.OVER_SCROLL_NEVER

                val compositeTransformer = CompositePageTransformer()
                compositeTransformer.addTransformer(MarginPageTransformer(40))
                compositeTransformer.addTransformer { page, position ->
                    val r = 1 - abs(position)
                    page.scaleY = (0.95f + r * 0.05f)
                }
                binding.viewPagerSubscription.setPageTransformer(compositeTransformer)

            }, 2000)
        }
    }*/

    // Step 5 : Launch the purchase flow

    fun launchPurchaseFlow(productDetails: ProductDetails) {
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

                //user prefs to set premium
                /* showSuccess(
                     this,
                     "Subscription activated, Enjoy!",
                 )*/
                //Setting premium to 1
                // 1 - premium
                // 0 - no premium
//                prefs.setPremium(1)
//                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
        Log.d(TAG, "Purchase Token: " + purchases.purchaseToken)
        Log.d(TAG, "Purchase Time: " + purchases.purchaseTime)
        Log.d(TAG, "Purchase OrderID: " + purchases.orderId)
    }


    override fun observeViewModel() {
        // Observe open subscription plan live data
        observe(subscriptionViewModel.openSubscriptionPlanLiveData, ::openSubscriptionPlan)

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
                }
            }
        }


    }

    private fun openSubscriptionPlan(singleEvent: SingleEvent<ProductDetails?>) {
        singleEvent.getContentIfNotHandled()?.let {
            Log.d(TAG, "openSubscriptionPlan: Clicked Plan is $it ")
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

}