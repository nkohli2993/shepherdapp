package com.shepherdapp.app.ui.component.my_subscription

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.billingclient.api.*
import com.google.gson.Gson
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentMySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.subscription.SubscriptionActivity
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.extensions.changeDatesFormat
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.MySubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Deepak Rattan on 25/11/22
 */
@AndroidEntryPoint
class MySubscriptionFragment : BaseFragment<FragmentMySubscriptionBinding>(), View.OnClickListener {
    private var fragmentMySubscriptionBinding: FragmentMySubscriptionBinding? = null
    private val mySubscriptionViewModel: MySubscriptionViewModel by viewModels()
    private var billingClient: BillingClient? = null
    private val TAG = "MySubscriptionFragment"
    private var transactionId: String? = null
    private var isActive: Boolean = false
    private var productId: String? = null
    private var url: String? = null
    private var page = 1
    private var limit = 10


    @SuppressLint("SetTextI18n")
    override fun observeViewModel() {
        // Get Active Subscriptions
        mySubscriptionViewModel.getActiveSubscriptionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    fragmentMySubscriptionBinding?.txtPlan?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.cv?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtExpirePlan?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtPlanExpireDate?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtRenew?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.btnChangePlan?.visibility = View.GONE

                    // Show No Subscription found
                    fragmentMySubscriptionBinding?.txtNoSubscriptionFound?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.txtRenewSubscription?.visibility = View.VISIBLE

                    // If no subscription found, get previous subscriptions
//                    mySubscriptionViewModel.getPreviousSubscriptions(page, limit)
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    fragmentMySubscriptionBinding?.txtPlan?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.cv?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.txtExpirePlan?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.txtPlanExpireDate?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.txtRenew?.visibility = View.VISIBLE
                    fragmentMySubscriptionBinding?.btnChangePlan?.visibility = View.VISIBLE

                    // Hide No Subscription found
                    fragmentMySubscriptionBinding?.txtNoSubscriptionFound?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtRenewSubscription?.visibility = View.GONE


                    it.data.payload.let { payload ->
                        fragmentMySubscriptionBinding?.txtPlanExpireDate?.text =
                            payload?.expiryDate.changeDatesFormat(
                                sourceFormat = "yyyy-MM-dd",
                                targetFormat = "dd MMM, yyyy"
                            )
                        fragmentMySubscriptionBinding?.txtLovedOne?.text =
                            "Max ${payload?.allowedLovedOnesCount} lovedOne can be added"
                        fragmentMySubscriptionBinding?.txtPrice?.text = payload?.amount.toString()
                        fragmentMySubscriptionBinding?.txtPriceUnit?.text = "$/${payload?.plan}"
                        fragmentMySubscriptionBinding?.txtTitle?.text = "${payload?.plan}"
                        fragmentMySubscriptionBinding?.txtPlan?.text =
                            "You have chosen ${payload?.plan} plan"

                        transactionId = payload?.transactionId
                        fragmentMySubscriptionBinding?.layoutCard!!.setBackgroundResource(R.drawable.bg_subscription_red)
                        if ((payload?.plan ?: "").lowercase().contains("year")) {
                            fragmentMySubscriptionBinding?.layoutCard!!.setBackgroundResource(R.drawable.bg_subscription_orange)
                        }
                    }
                    establishConnection()
                }
            }
        }

        // Get Previous Subscriptions
        /* mySubscriptionViewModel.getPreviousSubscriptionResponseLiveData.observeEvent(this) {
             when (it) {
                 is DataResult.Failure -> {
                     hideLoading()
                     showError(requireContext(), it.message.toString())
                 }
                 is DataResult.Loading -> {
                     showLoading("")
                 }
                 is DataResult.Success -> {
                     hideLoading()
                     val subscriptionData = it.data.payload?.users?.get(0)

                     fragmentMySubscriptionBinding?.txtPlan?.visibility = View.VISIBLE
                     fragmentMySubscriptionBinding?.cv?.visibility = View.VISIBLE
                     fragmentMySubscriptionBinding?.txtExpirePlan?.visibility = View.VISIBLE
                     fragmentMySubscriptionBinding?.txtPlanExpireDate?.visibility = View.VISIBLE
                     fragmentMySubscriptionBinding?.txtRenew?.visibility = View.VISIBLE
                     fragmentMySubscriptionBinding?.btnChangePlan?.visibility = View.VISIBLE

                     // Hide No Subscription found
                     fragmentMySubscriptionBinding?.txtNoSubscriptionFound?.visibility = View.GONE

                     it.data.payload.let { payload ->
                         fragmentMySubscriptionBinding?.txtPlanExpireDate?.text =
                             subscriptionData?.expiryDate.changeDatesFormat(
                                 sourceFormat = "yyyy-MM-dd",
                                 targetFormat = "dd MMM, yyyy"
                             )
                         fragmentMySubscriptionBinding?.txtLovedOne?.text =
                             "Max ${subscriptionData?.allowedLovedOnesCount} lovedOne can be added"
                         fragmentMySubscriptionBinding?.txtPrice?.text =
                             subscriptionData?.amount.toString()
                         fragmentMySubscriptionBinding?.txtPriceUnit?.text =
                             "$/${subscriptionData?.plan}"
                         fragmentMySubscriptionBinding?.txtTitle?.text = "${subscriptionData?.plan}"
                         fragmentMySubscriptionBinding?.txtPlan?.text =
                             "You have chosen ${subscriptionData?.plan} plan"

                         transactionId = subscriptionData?.transactionId
                     }
                     establishConnection()
                 }
             }
         }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMySubscriptionBinding =
            FragmentMySubscriptionBinding.inflate(inflater, container, false)
        return fragmentMySubscriptionBinding?.root!!
    }

    override fun initViewBinding() {
        fragmentMySubscriptionBinding?.listener = this

//        mySubscriptionViewModel.getActiveSubscriptions()

        //Step 2. Initialize a BillingClient with PurchasesUpdatedListener
        billingClient = BillingClient.newBuilder(requireContext())
            .enablePendingPurchases()
            .setListener { billingResult, list ->
                /* if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
                     for (purchase in list) {
                         verifySubPurchase(purchase)
                     }
                 }*/
            }.build()
    }

    private fun establishConnection() {
        // Connect to Google Play
        billingClient?.startConnection(object : BillingClientStateListener {

            override fun onBillingSetupFinished(billingResult: BillingResult) {

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.

                    billingClient?.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS).build()
                    ) { billingResult1: BillingResult, list: List<Purchase> ->

                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "List Size :${list.size} ")

                            // We will get active subscriptions if list id not empty
                            if (list.isNotEmpty()) {
                                isActive = true
                                for (purchase in list) {
                                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && purchase.isAcknowledged) {
                                        val originalJson = purchase.originalJson
                                        Log.d(TAG, "json : $originalJson")
                                        // Parse Json
                                        var map: Map<String, Any> = HashMap()
                                        map = Gson().fromJson(originalJson, map.javaClass)

                                        Log.d(TAG, "onBillingSetupFinished: map is $map")
                                        productId = map["productId"] as String?
                                        Log.d(TAG, "onBillingSetupFinished: productId : $productId")

                                        // Button text changes to Cancel
                                        activity?.runOnUiThread {
                                            fragmentMySubscriptionBinding?.txtRenew?.text =
                                                getString(R.string.cancel)
                                        }
                                    }
                                }
                            } else {
                                Log.d(
                                    TAG,
                                    "onBillingSetupFinished: Subscription is not active as list size is zero"
                                )
                                isActive = false
                                enableRenewButton()
                            }
                        } else {
                            isActive = false
                            Log.d(TAG, "onBillingSetupFinished: Subscription is not active")
                            enableRenewButton()
                        }
                    }
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection()
            }
        })
    }

    fun enableRenewButton() {
        // Enable the renew button
        activity?.runOnUiThread {
            if (isActive) {
                fragmentMySubscriptionBinding?.txtRenew?.visibility = View.VISIBLE
                fragmentMySubscriptionBinding?.txtRenew?.text = getString(R.string.cancel)
            } else {
                fragmentMySubscriptionBinding?.txtRenew?.visibility = View.VISIBLE
                fragmentMySubscriptionBinding?.txtRenew?.text = getString(R.string.renew)
                fragmentMySubscriptionBinding?.txtPlanExpireDate?.text = getString(R.string.expired)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_subscription
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                Log.d(TAG, "Count: ${parentFragmentManager.backStackEntryCount} ")
                backPress()
            }
            R.id.txtRenew -> {
                url = if (productId == null) {
                    // If the SKU is not specified, just open the Google Play subscriptions URL.
                    Const.PLAY_STORE_SUBSCRIPTION_URL
                } else {
                    // If the SKU is specified, open the deeplink for this SKU on Google Play.
                    String.format(
                        Const.PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL,
                        productId,
                        requireContext().applicationContext.packageName
                    )
                }
                val intent = Intent(Intent.ACTION_VIEW);
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
            R.id.txtRenewSubscription -> {
                // Redirect to Subscription Screen
                val intent = Intent(requireContext(), SubscriptionActivity::class.java)
                intent.putExtra("source", "My Subscription")
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

            R.id.btnChangePlan -> {
//                requireContext().startActivity<SubscriptionActivity>()

                // Redirect to Subscription Screen
                val intent = Intent(requireContext(), SubscriptionActivity::class.java)
                intent.putExtra("source", "My Subscription")
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        billingClient?.endConnection()
    }

    override fun onResume() {
        super.onResume()
        mySubscriptionViewModel.getActiveSubscriptions()
    }
}