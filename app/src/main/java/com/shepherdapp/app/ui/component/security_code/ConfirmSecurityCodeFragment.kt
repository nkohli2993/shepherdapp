package com.shepherdapp.app.ui.component.security_code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherdapp.app.databinding.FragmentConfirmSecurityCodeBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.extensions.otpHelper
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showInfo
import com.shepherdapp.app.view_model.SecurityCodeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmSecurityCodeFragment : BaseFragment<FragmentConfirmSecurityCodeBinding>(),
    View.OnClickListener {
    private val securityCodeViewModel: SecurityCodeViewModel by viewModels()
    private lateinit var fragmentConfirmSecurityCodeBinding: FragmentConfirmSecurityCodeBinding
    private var otp: String? = null
    private var code: String? = null
    private var type: String? = null
    private val args: ConfirmSecurityCodeFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentConfirmSecurityCodeBinding =
            FragmentConfirmSecurityCodeBinding.inflate(inflater, container, false)

        return fragmentConfirmSecurityCodeBinding.root
    }

    override fun observeViewModel() {
        securityCodeViewModel.changeSecurityCodeLiveData.observeEvent(this) { result ->
            when (result) {
                is DataResult.Failure -> {
                    hideLoading()
                    result.message?.let { showError(requireContext(), it) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
                    val payload = result.data.payload
                    securityCodeViewModel.saveDetail(
                        UserProfile(
                            payload!!.id,
                            payload.userId,
                            payload.firstname,
                            payload.lastname,
                            payload.dob,
                            payload.address,
                            payload.phoneCode,
                            payload.phoneNo,
                            payload.profilePhoto,
                            payload.isBiometric,
                            payload.isEmailVerified,
                            payload.createdAt,
                            payload.updatedAt,
                            payload.deletedAt,
                            payload.securityCode
                        )
                    )
                    when (type) {
                        Const.SET_SECURITY_CODE -> {
                            showInfo(
                                requireContext(),
                                getString(R.string.security_code_added_successfully)
                            )
                        }
                        Const.RESET_SECURITY_CODE -> {
                            showInfo(
                                requireContext(),
                                getString(R.string.security_code_reset_successfully)
                            )
                        }
                    }
                    findNavController().navigate(R.id.nav_setting)
                }
            }
        }

    }


    override fun initViewBinding() {
        type = args.type
        code = args.code
        fragmentConfirmSecurityCodeBinding.listener = this
        editTextHandlers()
        if (type != null) {
            when (type) {
                Const.SET_SECURITY_CODE -> {
                    fragmentConfirmSecurityCodeBinding.tvTitle.text =
                        getString(R.string.set_security_code)
                    fragmentConfirmSecurityCodeBinding.btnSaveChange.text = getString(R.string.set)
                }
                Const.RESET_SECURITY_CODE -> {
                    fragmentConfirmSecurityCodeBinding.tvTitle.text =
                        getString(R.string.reset_security_code)
                    fragmentConfirmSecurityCodeBinding.btnSaveChange.text =
                        getString(R.string.reset)
                }
            }
        }
    }

    private fun editTextHandlers() {
        fragmentConfirmSecurityCodeBinding.etFirst.otpHelper()
        fragmentConfirmSecurityCodeBinding.etSecond.otpHelper()
        fragmentConfirmSecurityCodeBinding.etThird.otpHelper()
        fragmentConfirmSecurityCodeBinding.etFourth.otpHelper()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_confirm_security_code
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSaveChange -> {
                otp = fragmentConfirmSecurityCodeBinding.etFirst.text.toString()
                    .plus(fragmentConfirmSecurityCodeBinding.etSecond.text.toString())
                    .plus(fragmentConfirmSecurityCodeBinding.etThird.text.toString())
                    .plus(fragmentConfirmSecurityCodeBinding.etFourth.text.toString())
                if (otp!!.length == 4 && otp == code) {
                    securityCodeViewModel.resetSecurityCode(SendSecurityCodeRequestModel(otp))
                } else {
                    if (otp!!.length < 4) {
                        showError(
                            requireContext(),
                            getString(R.string.please_enter_confirm_security_code)
                        )
                    } else if (otp != code) {
                        showError(
                            requireContext(),
                            getString(R.string.entered_confrim_code_doesnot_matched_with_entered_code)
                        )
                    }
                }
            }
        }
    }
}