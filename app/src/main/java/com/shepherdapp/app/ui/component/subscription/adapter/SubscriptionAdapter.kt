package com.shepherdapp.app.ui.component.subscription.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.ProductDetails
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.AdapterSubscriptionBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.SubscriptionViewModel

/**
 * Created by Deepak Rattan on 29/09/22
 */
class SubscriptionAdapter constructor(
    val context: Context,
    private val viewModel: SubscriptionViewModel,
//    private val list: List<SubscriptionModel>
    private var list: ArrayList<ProductDetails?>?
) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    lateinit var binding: AdapterSubscriptionBinding
    private var TAG = "SubscriptionAdapter"


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSubscriptionPlan(itemData[0] as ProductDetails)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
//        context = parent.context
        binding = AdapterSubscriptionBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return SubscriptionViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(position, list?.get(position), onItemClickListener)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${list?.size}")
        return list?.size ?: 0
    }


    class SubscriptionViewHolder(
        private val itemBinding: AdapterSubscriptionBinding,
        val context: Context
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(
            position: Int,
            productDetails: ProductDetails?,
            recyclerItemListener: RecyclerItemListener
        ) {

//            itemBinding.data = subscriptionModel
            if (position % 2 == 0) {
                /* itemBinding.layoutCard.setBackgroundColor(
                     ContextCompat.getColor(
                         context,
                         R.color._FF9AA6
                     )
                 )*/
                itemBinding.cvSubscription.setBackgroundResource(R.drawable.bg_subscription_orange)
            } else {
                /* itemBinding.layoutCard.setBackgroundColor(
                     ContextCompat.getColor(
                         context,
                         R.color._7ECEFF
                     )
                 )*/
                itemBinding.cvSubscription.setBackgroundResource(R.drawable.bg_subscription_blue)

            }

            itemBinding.txtTitle.text = productDetails?.name
            itemBinding.txtDesc.text = context.getString(R.string.max_3_loved_one_can_be_added)
            itemBinding.txtPrice.text =
                productDetails?.subscriptionOfferDetails?.get(0)?.pricingPhases?.pricingPhaseList?.get(
                    0
                )?.formattedPrice

            // Handle click of Buy Plan Button
            itemBinding.btnBuyPlan.setOnClickListener {
                if (productDetails != null) {
                    recyclerItemListener.onItemSelected(
                        productDetails
                    )
                }
            }
        }
    }


    fun addData(productList: ArrayList<ProductDetails?>?) {
        this.list = productList
        notifyDataSetChanged()
    }
}