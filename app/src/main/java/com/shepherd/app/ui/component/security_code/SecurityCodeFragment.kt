package com.shepherd.app.ui.component.security_code


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.poovam.pinedittextfield.PinField
import com.shepherd.app.R
import com.shepherd.app.data.dto.security_code.SendSecurityCodeRequestModel
import com.shepherd.app.databinding.FragmentSecurityCodeBinding
import com.shepherd.app.network.retrofit.DataResult
import com.shepherd.app.network.retrofit.observeEvent
import com.shepherd.app.ui.base.BaseFragment
import com.shepherd.app.ui.component.carePoints.CarePointDetailFragmentArgs
import com.shepherd.app.ui.component.settings.SettingFragmentDirections
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.extensions.hideKeyboard
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.SecurityCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.annotations.NotNull


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
        fragmentSecurityCodeBinding.squareField.highlightPaintColor =
            ContextCompat.getColor(requireContext(), R.color._192032)
        fragmentSecurityCodeBinding.squareField.fieldColor =
            ContextCompat.getColor(requireContext(), R.color._192032)
        fragmentSecurityCodeBinding.squareField.onTextCompleteListener =
            object : PinField.OnTextCompleteListener {
                override fun onTextComplete(@NotNull enteredText: String): Boolean {
                    Toast.makeText(requireContext(), enteredText, Toast.LENGTH_SHORT).show()
                    return true
                }
            }

        editTextViewHandling()
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

    private fun editTextViewHandling() {
        fragmentSecurityCodeBinding.etFirst.isLongClickable = false
        fragmentSecurityCodeBinding.etSecond.isLongClickable = false
        fragmentSecurityCodeBinding.etThird.isLongClickable = false
        fragmentSecurityCodeBinding.etFourth.isLongClickable = false
        fragmentSecurityCodeBinding.etFirst.doOnTextChanged { text, start, before, count ->
            if (text.toString().length == 1 && before == 0) {
                fragmentSecurityCodeBinding.etSecond.requestFocus()
            }

        }
        fragmentSecurityCodeBinding.etSecond.doOnTextChanged { text, start, before, count ->
            editTextHandling(
                text,
                before,
                count,
                fragmentSecurityCodeBinding.etFirst,
                fragmentSecurityCodeBinding.etThird
            )
        }
        fragmentSecurityCodeBinding.etThird.doOnTextChanged { text, start, before, count ->
            editTextHandling(
                text,
                before,
                count,
                fragmentSecurityCodeBinding.etSecond,
                fragmentSecurityCodeBinding.etFourth
            )
        }
        fragmentSecurityCodeBinding.etFourth.doOnTextChanged { text, start, before, count ->
            if (text.toString().length == 1 && before == 0) {
                hideKeyboard()
            } else if (count == 0) {
                fragmentSecurityCodeBinding.etThird.requestFocus()
            }
        }
    }

    private fun editTextHandling(
        text: CharSequence?,
        before: Int,
        count: Int,
        view: AppCompatEditText,
        viewSecond: AppCompatEditText
    ) {
        if (text.toString().length == 1 && before == 0) {
            viewSecond.requestFocus()
        } else if (count == 0) {
            view.requestFocus()
        }
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
}