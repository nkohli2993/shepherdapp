package com.shepherdapp.app.ui.component.settings

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherdapp.app.BuildConfig
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.enterprise.AttachEnterpriseRequestModel
import com.shepherdapp.app.databinding.FragmentSettingBinding
import com.shepherdapp.app.network.retrofit.DataResult
import com.shepherdapp.app.network.retrofit.observeEvent
import com.shepherdapp.app.ui.base.BaseFragment
import com.shepherdapp.app.ui.component.home.HomeActivity
import com.shepherdapp.app.utils.BiometricUtils
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.extensions.showSuccess
import com.shepherdapp.app.view_model.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(), View.OnClickListener {
    private lateinit var fragmentSettingBinding: FragmentSettingBinding
    private val settingViewModel: SettingViewModel by viewModels()
    private lateinit var homeActivity: HomeActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeActivity) {
            homeActivity = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSettingBinding =
            FragmentSettingBinding.inflate(inflater, container, false)

        return fragmentSettingBinding.root
    }

    override fun observeViewModel() {
        settingViewModel.bioMetricLiveData.observeEvent(this) {
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
                    it.data.let { it1 ->
                        // Save Token to SharedPref
                        it1.payload?.let { payload ->
                            Prefs.with(requireContext())!!
                                .save(Const.BIOMETRIC_ENABLE, payload.userProfiles?.isBiometric!!)
                        }
                    }
                }
            }
        }

        settingViewModel.attachEnterpriseLiveData.observeEvent(this) {
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
                    showSuccess(requireContext(), it.data.message.toString())
                }
            }
        }
    }

    override fun initViewBinding() {
        fragmentSettingBinding.listener = this
        if (BiometricUtils.isSdkVersionSupported && BiometricUtils.isHardwareSupported(
                requireContext()
            ) && BiometricUtils.isFingerprintAvailable(
                requireContext()
            )
        ) {
            fragmentSettingBinding.scBioMetric.apply {
                isChecked = Prefs.with(requireContext())!!.getBoolean(Const.BIOMETRIC_ENABLE)
                setOnCheckedChangeListener { _, isChecked ->
                    registerBiometric(isChecked)

                }
            }
        } else {
            fragmentSettingBinding.clBioMetricLogin.isVisible = false
        }
        fragmentSettingBinding.tvReset.visibility = View.VISIBLE
        fragmentSettingBinding.tvSet.visibility = View.GONE
        if (settingViewModel.getUserDetail()?.security_code == null || settingViewModel.getUserDetail()?.security_code!!.isEmpty()) {
            fragmentSettingBinding.tvReset.visibility = View.GONE
            fragmentSettingBinding.tvSet.visibility = View.VISIBLE
        }
        fragmentSettingBinding.tvVersion.text = "V: ${BuildConfig.VERSION_NAME}"

        if (settingViewModel.isUserAttachedToEnterprise() == true) {
            fragmentSettingBinding.txtEnterprise.text = getString(R.string.enterprise_detail)
            fragmentSettingBinding.txtEnterpriseName.visibility = View.VISIBLE
            fragmentSettingBinding.clSubscription.visibility = View.GONE
            // To make the view non-clickable
            fragmentSettingBinding.clBecomeAnEnterpriseUser.isEnabled = false
        }
    }

    private fun registerBiometric(checked: Boolean) {
        settingViewModel.registerBioMetric(checked)
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_setting
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                homeActivity.onBackPressed()
            }
            R.id.tvChange -> {
                findNavController().navigate(R.id.action_nav_setting_to_changePassword)
            }
            R.id.tvReset -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToSecureCode(
                        source = Const.RESET_SECURITY_CODE
                    )
                )
            }
            R.id.tvSet -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToSecureCode(
                        source = Const.SET_SECURITY_CODE
                    )
                )
            }
            R.id.clInvitations -> {
                findNavController().navigate(R.id.action_nav_setting_to_invitation)

            }
            R.id.clBecomeAnEnterpriseUser -> {
                showEnterpriseCodeDialog()
            }
            R.id.clAboutUs -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToInformation(
                        source = Const.ABOUT_US
                    )
                )
            }
            R.id.clPrivacyPolicy -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToInformation(
                        source = Const.PRIVACY_POLICY
                    )
                )
            }
            R.id.clTermOfUse -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToInformation(
                        source = Const.TERM_OF_USE
                    )
                )
            }
            R.id.tvLogout -> {
                homeActivity.viewModel.logOut()
            }
        }
    }


    // Enter Enterprise code dialog
    private fun showEnterpriseCodeDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_enterprise_code)
        val edtEnterCode = dialog.findViewById(R.id.edtEnterCode) as EditText
        val btnSubmit = dialog.findViewById(R.id.btnSubmit) as TextView
        val btnCancel = dialog.findViewById(R.id.btnCancel) as TextView

        btnSubmit.setOnClickListener {
            val enterpriseCode = edtEnterCode.text.toString().trim()
            if (enterpriseCode.isNotEmpty()) {
                settingViewModel.attachEnterprise(AttachEnterpriseRequestModel(enterpriseCode))
            }
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawable(
            InsetDrawable(
                ColorDrawable(Color.TRANSPARENT),
                20
            )
        )
        dialog.show()
    }

}