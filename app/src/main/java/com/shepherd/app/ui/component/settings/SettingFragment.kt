package com.shepherd.app.ui.component.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.shepherd.app.R
import com.shepherd.app.databinding.FragmentSettingBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.home.HomeActivity
import com.shepherd.app.utils.BiometricUtils
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.extensions.showError
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
    }

    private fun registerBiometric(checked: Boolean) {
        settingViewModel.registerBioMetric(
            checked
        )
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_setting
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivBack -> {
                findNavController().popBackStack()
            }
            R.id.tvChange -> {
                findNavController().navigate(R.id.action_nav_setting_to_changePassword)
            }
            R.id.tvReset -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToInformation(
                        source = Const.RESET_SECURITY_CODE
                    )
                )

                //findNavController().navigate(R.id.action_nav_setting_to_secureCode)
            }
            R.id.tvSet -> {
                findNavController().navigate(
                    SettingFragmentDirections.actionNavSettingToInformation(
                        source = Const.SET_SECURITY_CODE
                    )
                )

                //findNavController().navigate(R.id.action_nav_setting_to_secureCode)
            }
            R.id.clInvitations -> {
                findNavController().navigate(R.id.action_nav_setting_to_invitation)

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
}