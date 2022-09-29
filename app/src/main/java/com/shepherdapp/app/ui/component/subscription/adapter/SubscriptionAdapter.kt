package com.shepherdapp.app.ui.component.subscription.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.subscription.SubscriptionModel
import com.shepherdapp.app.databinding.AdapterSubscriptionBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.SubscriptionViewModel

/**
 * Created by Deepak Rattan on 29/09/22
 */
class SubscriptionAdapter(
    private val viewModel: SubscriptionViewModel,
    private val list: List<SubscriptionModel>
) :
    RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    lateinit var binding: AdapterSubscriptionBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSubscriptionPlan(itemData[0] as SubscriptionModel)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        context = parent.context
        binding =
            AdapterSubscriptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SubscriptionViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.bind(list[position], onItemClickListener)
    }

    override fun getItemCount(): Int = list.size


    class SubscriptionViewHolder(
        private val itemBinding: AdapterSubscriptionBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(subscriptionModel: SubscriptionModel, recyclerItemListener: RecyclerItemListener) {

            itemBinding.data = subscriptionModel

            // Handle click of Buy Plan Button
            itemBinding.btnBuyPlan.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    subscriptionModel
                )
            }
        }
    }
}