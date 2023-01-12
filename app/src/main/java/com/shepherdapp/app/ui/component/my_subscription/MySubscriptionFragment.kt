package com.shepherdapp.app.ui.component.my_subscription

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.android.billingclient.api.*
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.subscription.purchase.PurchaseJson
import com.shepherdapp.app.databinding.FragmentMySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.subscription.SubscriptionActivity
import com.shepherdapp.app.utils.extensions.changeDatesFormat
import com.shepherdapp.app.utils.toKotlinObject
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


    override fun observeViewModel() {
        mySubscriptionViewModel.getActiveSubscriptionResponseLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
//                    showError(requireContext(), it.message.toString())
                    fragmentMySubscriptionBinding?.txtPlan?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.cv?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtExpirePlan?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtPlanExpireDate?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.txtRenew?.visibility = View.GONE
                    fragmentMySubscriptionBinding?.btnChangePlan?.visibility = View.GONE

                    // Show No Subscription found
                    fragmentMySubscriptionBinding?.txtNoSubscriptionFound?.visibility = View.VISIBLE
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
                    }
                    establishConnection()
                }
            }
        }
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

        mySubscriptionViewModel.getActiveSubscriptions()

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
                                        val json = purchase.originalJson

                                        Log.d(TAG, "json : $json")
                                        // Convert JSON to model class
                                        val purchaseJson = json.toKotlinObject<PurchaseJson>()
                                        Log.d(
                                            TAG,
                                            "Subscription is ACTIVE for orderId : ${purchaseJson.orderId} "
                                        )
                                        /*  if (transactionId == purchaseJson.orderId) {

                                          } else {

                                          }*/

                                        // Disable the renew button
                                        activity?.runOnUiThread {
                                            fragmentMySubscriptionBinding?.txtRenew?.visibility =
                                                View.GONE
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
            fragmentMySubscriptionBinding?.txtRenew?.visibility = View.VISIBLE
            fragmentMySubscriptionBinding?.txtPlanExpireDate?.text = "Expired"
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

            }
            R.id.btnChangePlan -> {
                requireContext().startActivity<SubscriptionActivity>()
            }
        }
    }

    /* override fun onResume() {
         super.onResume()
         Log.d(TAG, "onResume: isActive : $isActive")
         if (isActive) {

         } else {
             fragmentMySubscriptionBinding.txtRenew.visibility = View.VISIBLE
             fragmentMySubscriptionBinding.txtPlanExpireDate.text = "Expired"
         }
     }*/


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        billingClient?.endConnection()
    }
}