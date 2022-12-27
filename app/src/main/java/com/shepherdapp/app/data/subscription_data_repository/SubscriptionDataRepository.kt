package com.shepherdapp.app.data.subscription_data_repository

/**
 * Created by Deepak Rattan on 30/09/22
 */

import com.shepherdapp.app.billing.BillingClientWrapper
import kotlinx.coroutines.flow.Flow


/**
 * The [SubscriptionDataRepository] processes and tranforms the [StateFlow] data received from
 * the [BillingClientWrapper] into [Flow] data available to the viewModel.
 *
 */
class SubscriptionDataRepository {}

/*@Singleton
class SubscriptionDataRepository(billingClientWrapper: BillingClientWrapper) {

    // Set to true when a returned purchase is an auto-renewing basic subscription.
    val hasRenewableBasic: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            purchase.products.contains(BASIC_SUB) && purchase.isAutoRenewing
        }
    }

    // Set to true when a returned purchase is prepaid basic subscription.
    val hasPrepaidBasic: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            !purchase.isAutoRenewing && purchase.products.contains(BASIC_SUB)
        }
    }

    // Set to true when a returned purchases is an auto-renewing premium subscription.
    val hasRenewablePremium: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            purchase.products.contains(PREMIUM_SUB) && purchase.isAutoRenewing
        }
    }

    // Set to true when a returned purchase is prepaid premium subscription.
    val hasPrepaidPremium: Flow<Boolean> = billingClientWrapper.purchases.map { purchaseList ->
        purchaseList.any { purchase ->
            !purchase.isAutoRenewing && purchase.products.contains(PREMIUM_SUB)
        }
    }

    // ProductDetails for the basic subscription.
    val basicProductDetails: Flow<ProductDetails> =
        billingClientWrapper.productWithProductDetails.filter {
            it.containsKey(
                BASIC_SUB
            )
        }.map { it[BASIC_SUB]!! }

    // ProductDetails for the premium subscription.
    val premiumProductDetails: Flow<ProductDetails> =
        billingClientWrapper.productWithProductDetails.filter {
            it.containsKey(
                PREMIUM_SUB
            )
        }.map { it[PREMIUM_SUB]!! }

    // List of current purchases returned by the Google PLay Billing client library.
    val purchases: Flow<List<Purchase>> = billingClientWrapper.purchases

    // Set to true when a purchase is acknowledged.
    val isNewPurchaseAcknowledged: Flow<Boolean> = billingClientWrapper.isNewPurchaseAcknowledged

    companion object {
        // List of subscription product offerings
        private const val BASIC_SUB = "up_basic_sub"
        private const val PREMIUM_SUB = "up_premium_sub"
    }
}*/
