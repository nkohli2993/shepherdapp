package com.shepherd.app.ui.component.security_code


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentSecurityCodeBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.extensions.otpHelper
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.SecurityCodeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class SecurityCodeFragment : BaseFragment<FragmentSecurityCodeBinding>(), View.OnClickListener {
    private val securityCodeViewModel: SecurityCodeViewModel by viewModels()
    private lateinit var fragmentSecurityCodeBinding: FragmentSecurityCodeBinding
    private var otp: String? = null
    private var type: String? = null
    private val args: SecurityCodeFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSecurityCodeBinding =
            FragmentSecurityCodeBinding.inflate(inflater, container, false)

        return fragmentSecurityCodeBinding.root
    }

    override fun observeViewModel() {
        securityCodeViewModel.addSecurityCodeLiveData.observeEvent(this) {
            when (it) {
                is DataResult.Failure -> {
                    hideLoading()
                    it.message?.let { showError(requireContext(), it.toString()) }
                }
                is DataResult.Loading -> {
                    showLoading("")
                }
                is DataResult.Success -> {
                    hideLoading()
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
                    backPress()
                }
            }
        }

    }


    override fun initViewBinding() {
        type = args.source
        fragmentSecurityCodeBinding.listener = this
        //editTextViewHandling()
        editTextHandlers()
        if (type != null) {
            when (type) {
                Const.SET_SECURITY_CODE -> {
                    fragmentSecurityCodeBinding.tvTitle.text = getString(R.string.set_security_code)
                }
                Const.RESET_SECURITY_CODE -> {
                    fragmentSecurityCodeBinding.tvTitle.text =
                        getString(R.string.reset_security_code)
                }
            }
        }
    }

    private fun editTextHandlers() {
        fragmentSecurityCodeBinding.etFirst.otpHelper()
        fragmentSecurityCodeBinding.etSecond.otpHelper()
        fragmentSecurityCodeBinding.etThird.otpHelper()
        fragmentSecurityCodeBinding.etFourth.otpHelper()
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_security_code
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.btnSaveChange -> {
                otp = fragmentSecurityCodeBinding.etFirst.text.toString()
                    .plus(fragmentSecurityCodeBinding.etSecond.text.toString())
                    .plus(fragmentSecurityCodeBinding.etThird.text.toString())
                    .plus(fragmentSecurityCodeBinding.etFourth.text.toString())

                if (otp!!.length == 4) {
                    findNavController().navigate(
                        SecurityCodeFragmentDirections.actionConfirmSecureCode(
                            type = type!!, code = otp!!
                        )
                    )
                } else {
                    showError(requireContext(), getString(R.string.please_enter_confirm_code))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentSecurityCodeBinding.etFirst.setText("")
        fragmentSecurityCodeBinding.etSecond.setText("")
        fragmentSecurityCodeBinding.etThird.setText("")
        fragmentSecurityCodeBinding.etFourth.setText("")
        fragmentSecurityCodeBinding.etFirst.clearFocus()
        fragmentSecurityCodeBinding.etSecond.clearFocus()
        fragmentSecurityCodeBinding.etThird.clearFocus()
        fragmentSecurityCodeBinding.etFourth.clearFocus()
        otp = null
    }
}