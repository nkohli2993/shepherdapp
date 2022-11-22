package com.shepherdapp.app.view_model

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.shepherdapp.app.billing.BillingClientWrapper
import com.shepherdapp.app.data.DataRepository
import com.shepherdapp.app.data.dto.subscription.SubscriptionRequestModel
import com.shepherdapp.app.data.dto.subscription.SubscriptionResponseModel
import com.shepherdapp.app.data.local.UserRepository
import com.shepherdapp.app.data.remote.lock_box.LockBoxRepository
import com.shepherdapp.app.data.remote.subscription.SubscriptionRepository
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.Event
import com.shepherdapp.app.ui.base.BaseViewModel
import com.shepherdapp.app.utils.SingleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Deepak Rattan
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val lockBoxRepository: LockBoxRepository,
    private val userRepository: UserRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val application: Application
) : BaseViewModel() {


    // Buy Subscription Plan Live Data
    /*private val _openSubscriptionPlanLiveData = MutableLiveData<SingleEvent<SubscriptionModel>>()
    val openSubscriptionPlanLiveData: LiveData<SingleEvent<SubscriptionModel>> get() = _openSubscriptionPlanLiveData
*/
    private val _openSubscriptionPlanLiveData = MutableLiveData<SingleEvent<ProductDetails?>>()
    val openSubscriptionPlanLiveData: LiveData<SingleEvent<ProductDetails?>> get() = _openSubscriptionPlanLiveData

    private var _createSubscriptionResponseLiveData =
        MutableLiveData<Event<DataResult<SubscriptionResponseModel>>>()
    var createSubscriptionResponseLiveData: LiveData<Event<DataResult<SubscriptionResponseModel>>> =
        _createSubscriptionResponseLiveData

    fun openSubscriptionPlan(productDetails: ProductDetails?) {
        _openSubscriptionPlanLiveData.value = SingleEvent(productDetails)
    }

    fun createSubscription(subscriptionRequestModel: SubscriptionRequestModel): LiveData<Event<DataResult<SubscriptionResponseModel>>> {
        viewModelScope.launch {
            val response = subscriptionRepository.createSubscription(subscriptionRequestModel)
            withContext(Dispatchers.Main) {
                response.collect {
                    _createSubscriptionResponseLiveData.postValue(Event(it))
                }
            }
        }
        return createSubscriptionResponseLiveData
    }


    var billingClient: BillingClientWrapper = BillingClientWrapper(application)

    /* private var repo: SubscriptionDataRepository =
         SubscriptionDataRepository(billingClientWrapper = billingClient)*/

    private val _billingConnectionState = MutableLiveData(false)
    val billingConnectionState: LiveData<Boolean> = _billingConnectionState

    private val _destinationScreen = MutableLiveData<DestinationScreen>()
    val destinationScreen: LiveData<DestinationScreen> = _destinationScreen

    // Start the billing connection when the viewModel is initialized.
    init {
        billingClient.startBillingConnection(billingConnectionState = _billingConnectionState)
    }


    // The productsForSaleFlows object combines all the Product flows into one for emission.
    /* val productsForSaleFlows = combine(
         repo.basicProductDetails,
         repo.premiumProductDetails
     ) { basicProductDetails,
         premiumProductDetails
         ->
         MainState(
             basicProductDetails = basicProductDetails,
             premiumProductDetails = premiumProductDetails
         )
     }*/

    // The userCurrentSubscriptionFlow object combines all the possible subscription flows into one
    // for emission.
    /* private val userCurrentSubscriptionFlow = combine(
         repo.hasRenewableBasic,
         repo.hasPrepaidBasic,
         repo.hasRenewablePremium,
         repo.hasPrepaidPremium
     ) { hasRenewableBasic,
         hasPrepaidBasic,
         hasRenewablePremium,
         hasPrepaidPremium
         ->
         MainState(
             hasRenewableBasic = hasRenewableBasic,
             hasPrepaidBasic = hasPrepaidBasic,
             hasRenewablePremium = hasRenewablePremium,
             hasPrepaidPremium = hasPrepaidPremium
         )
     }*/

    // Current purchases.
//    val currentPurchasesFlow = repo.purchases

    init {
        /* viewModelScope.launch {
             userCurrentSubscriptionFlow.collectLatest { collectedSubscriptions ->
                 when {
                     collectedSubscriptions.hasRenewableBasic == true &&
                             collectedSubscriptions.hasRenewablePremium == false -> {
                         _destinationScreen.postValue(DestinationScreen.BASIC_RENEWABLE_PROFILE)
                     }
                     collectedSubscriptions.hasRenewablePremium == true &&
                             collectedSubscriptions.hasRenewableBasic == false -> {
                         _destinationScreen.postValue(DestinationScreen.PREMIUM_RENEWABLE_PROFILE)
                     }
                     collectedSubscriptions.hasPrepaidBasic == true &&
                             collectedSubscriptions.hasPrepaidPremium == false -> {
                         _destinationScreen.postValue(DestinationScreen.BASIC_PREPAID_PROFILE_SCREEN)
                     }
                     collectedSubscriptions.hasPrepaidPremium == true &&
                             collectedSubscriptions.hasPrepaidBasic == false -> {
                         _destinationScreen.postValue(
                             DestinationScreen.PREMIUM_PREPAID_PROFILE_SCREEN
                         )
                     }
                     else -> {
                         _destinationScreen.postValue(DestinationScreen.SUBSCRIPTIONS_OPTIONS_SCREEN)
                     }
                 }
             }

         }*/
    }

    /**
     * Retrieves all eligible base plans and offers using tags from ProductDetails.
     *
     * @param offerDetails offerDetails from a ProductDetails returned by the library.
     * @param tag string representing tags associated with offers and base plans.
     *
     * @return the eligible offers and base plans in a list.
     *
     */
    private fun retrieveEligibleOffers(
        offerDetails: MutableList<ProductDetails.SubscriptionOfferDetails>,
        tag: String
    ): List<ProductDetails.SubscriptionOfferDetails> {
        val eligibleOffers = emptyList<ProductDetails.SubscriptionOfferDetails>().toMutableList()
        offerDetails.forEach { offerDetail ->
            if (offerDetail.offerTags.contains(tag)) {
                eligibleOffers.add(offerDetail)
            }
        }

        return eligibleOffers
    }

    /**
     * Calculates the lowest priced offer amongst all eligible offers.
     * In this implementation the lowest price of all offers' pricing phases is returned.
     * It's possible the logic can be implemented differently.
     * For example, the lowest average price in terms of month could be returned instead.
     *
     * @param offerDetails List of of eligible offers and base plans.
     *
     * @return the offer id token of the lowest priced offer.
     */
    private fun leastPricedOfferToken(
        offerDetails: List<ProductDetails.SubscriptionOfferDetails>
    ): String {
        var offerToken = String()
        var leastPricedOffer: ProductDetails.SubscriptionOfferDetails
        var lowestPrice = Int.MAX_VALUE

        if (!offerDetails.isNullOrEmpty()) {
            for (offer in offerDetails) {
                for (price in offer.pricingPhases.pricingPhaseList) {
                    if (price.priceAmountMicros < lowestPrice) {
                        lowestPrice = price.priceAmountMicros.toInt()
                        leastPricedOffer = offer
                        offerToken = leastPricedOffer.offerToken
                    }
                }
            }
        }
        return offerToken
    }

    /**
     * BillingFlowParams Builder for upgrades and downgrades.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * [leastPricedOfferToken].
     * @param oldToken the purchase token of the subscription purchase being upgraded or downgraded.
     *
     * @return [BillingFlowParams] builder.
     */
    private fun upDowngradeBillingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String,
        oldToken: String
    ): BillingFlowParams {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        ).setSubscriptionUpdateParams(
            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                .setOldPurchaseToken(oldToken)
                .setReplaceProrationMode(
                    BillingFlowParams.ProrationMode.IMMEDIATE_AND_CHARGE_FULL_PRICE
                )
                .build()
        ).build()
    }

    /**
     * BillingFlowParams Builder for normal purchases.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * [leastPricedOfferToken].
     *
     * @return [BillingFlowParams] builder.
     */
    private fun billingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String
    ): BillingFlowParams.Builder {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        )
    }

    /**
     * Use the Google Play Billing Library to make a purchase.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param currentPurchases List of current [Purchase] objects needed for upgrades or downgrades.
     * @param billingClient Instance of [BillingClientWrapper].
     * @param activity [Activity] instance.
     * @param tag String representing tags associated with offers and base plans.
     */
    fun buy(
        productDetails: ProductDetails,
        currentPurchases: List<Purchase>?,
        activity: Activity,
        tag: String
    ) {
        val offers =
            productDetails.subscriptionOfferDetails?.let {
                retrieveEligibleOffers(
                    offerDetails = it,
                    tag = tag.lowercase()
                )
            }
        val offerToken = offers?.let { leastPricedOfferToken(it) }
        val oldPurchaseToken: String

        // Get current purchase. In this app, a user can only have one current purchase at
        // any given time.
        if (!currentPurchases.isNullOrEmpty() &&
            currentPurchases.size == MAX_CURRENT_PURCHASES_ALLOWED
        ) {
            // This either an upgrade, downgrade, or conversion purchase.
            val currentPurchase = currentPurchases.first()

            // Get the token from current purchase.
            oldPurchaseToken = currentPurchase.purchaseToken

            val billingParams = offerToken?.let {
                upDowngradeBillingFlowParamsBuilder(
                    productDetails = productDetails,
                    offerToken = it,
                    oldToken = oldPurchaseToken
                )
            }

            if (billingParams != null) {
                billingClient.launchBillingFlow(
                    activity,
                    billingParams
                )
            }
        } else if (currentPurchases == null) {
            // This is a normal purchase.
            val billingParams = offerToken?.let {
                billingFlowParamsBuilder(
                    productDetails = productDetails,
                    offerToken = it
                )
            }

            if (billingParams != null) {
                billingClient.launchBillingFlow(
                    activity,
                    billingParams.build()
                )
            }
        } else if (!currentPurchases.isNullOrEmpty() &&
            currentPurchases.size > MAX_CURRENT_PURCHASES_ALLOWED
        ) {
            // The developer has allowed users  to have more than 1 purchase, so they need to
            /// implement a logic to find which one to use.
            Log.d(TAG, "User has more than 1 current purchase.")
        }
    }

    // When an activity is destroyed the viewModel's onCleared is called, so we terminate the
    // billing connection.
    override fun onCleared() {
        billingClient.terminateBillingConnection()
    }

    /**
     * Enum representing the various screens a user can be redirected to.
     */
    enum class DestinationScreen {
        SUBSCRIPTIONS_OPTIONS_SCREEN,
        BASIC_PREPAID_PROFILE_SCREEN,
        BASIC_RENEWABLE_PROFILE,
        PREMIUM_PREPAID_PROFILE_SCREEN,
        PREMIUM_RENEWABLE_PROFILE;
    }

    companion object {
        private const val TAG: String = "MainViewModel"

        private const val MAX_CURRENT_PURCHASES_ALLOWED = 1
    }


}
