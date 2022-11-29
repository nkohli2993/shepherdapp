package com.shepherdapp.app.ui.component.my_subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shepherdapp.app.R
import com.shepherdapp.app.databinding.FragmentMySubscriptionBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.subscription.SubscriptionActivity
import com.shepherdapp.app.utils.extensions.changeDatesFormat
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.view_model.MySubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Deepak Rattan on 25/11/22
 */
@AndroidEntryPoint
class MySubscriptionFragment : BaseFragment<FragmentMySubscriptionBinding>(), View.OnClickListener {
    private lateinit var fragmentMySubscriptionBinding: FragmentMySubscriptionBinding
    private val mySubscriptionViewModel: MySubscriptionViewModel by viewModels()

    override fun observeViewModel() {
        mySubscriptionViewModel.getActiveSubscriptionResponseLiveData.observeEvent(this) {
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
                    it.data.payload.let { payload ->
                        fragmentMySubscriptionBinding.txtPlanExpireDate.text =
                            payload?.expiryDate.changeDatesFormat(
                                sourceFormat = "yyyy-MM-dd",
                                targetFormat = "dd MMM, yyyy"
                            )
                        fragmentMySubscriptionBinding.txtLovedOne.text =
                            "Max ${payload?.allowedLovedOnesCount} lovedOne can be added"
                        fragmentMySubscriptionBinding.txtPrice.text = payload?.amount.toString()
                        fragmentMySubscriptionBinding.txtPriceUnit.text = "$/${payload?.plan}"
                        fragmentMySubscriptionBinding.txtTitle.text = "${payload?.plan}"
                        fragmentMySubscriptionBinding.txtPlan.text =
                            "You have chosen ${payload?.plan} plan"
                    }
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
        return fragmentMySubscriptionBinding.root
    }

    override fun initViewBinding() {
        fragmentMySubscriptionBinding.listener = this
        mySubscriptionViewModel.getActiveSubscriptions()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_my_subscription
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                backPress()
            }
            R.id.txtRenew -> {

            }
            R.id.btnChangePlan -> {
                requireContext().startActivity<SubscriptionActivity>()
            }
        }
    }
}